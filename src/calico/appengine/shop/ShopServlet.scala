package calico.appengine.shop

import com.thinkminimo.step.Step
import calico.appengine.shop.templating.Templating
import com.google.appengine.api.users.UserServiceFactory
import calico.appengine.shop.data.PMF.{save, query, remove, getSingle => single, using}
import calico.appengine.shop.data.{ShopList, UserList, Item}
import com.google.appengine.api.datastore.KeyFactory;

class ShopServlet extends Step with Templating {
  type Model =  {
    def getId : Long
    def getName : String
  }
  
  def getEmail = UserServiceFactory.getUserService.getCurrentUser.getEmail
  
  override def init() {
    loadTemplates(getServletContext.getRealPath("/templates/"))
    
    beforeTemplate += "header"
    afterTemplate += "footer"
    
    load("item", classOf[Item])
    
    loadList(List("header", "user", "logout"), "string") { (p, obj) =>
      obj.toString
    }
    
    load("list", "name") { _.asInstanceOf[Model].getName }
    load("list", "id") { _.asInstanceOf[Model].getId }
    load("item", "list") { obj =>
      obj.asInstanceOf[List[Item]].foldLeft("") {(str, i) =>
        str + template("item", ("item", i))
      }
    }
    load("item", "list-friendly") { obj =>
      obj.asInstanceOf[List[Item]].foldLeft("") {(str, i) =>
        str + template("list-friendly", ("item", i))
      }
    }
    load("item", "print") { i =>
      val item = i.asInstanceOf[Item]
      (if(!item.getQuantity.equals(1)) item.getQuantity + " " else "") + 
        item.getName + 
      (if(!item.getPrice.equals(0.0)) " @ $" +item.getPrice else "")
    }
    
    load("lists", "iterate") { obj =>
      obj.asInstanceOf[List[UserList]].foldLeft("") { (str, l) =>
        str + template("listrow", ("list", single(classOf[ShopList], l.getListId)))
      }
    }
  }
  
  before {
    contentType = "text/html"
  }
  
  def master(title: String) = {
    templateWrap(("header", title)) _
  }
  
  get("/viewlist/:listid") {
    val list = single(classOf[ShopList], params(":listid").toLong)
    val items = query { q =>
      q.setClass(classOf[Item])
      q.setFilter("listid == listidParam")
      q.setOrdering("id desc")
      q.execute(params(":listid").toLong)
    }
    
    template("view", ("list", list), ("item", items))
  }
  
  get("/") {
    val userservice = UserServiceFactory.getUserService
    val url = request.getRequestURI

    val name = request.getUserPrincipal.getName
    master("Shop") {
      template("index", ("user", name), ("logout", userservice.createLogoutURL(url)))
    }
  }
  
  get("/createlist/:listname") {
    val list = new ShopList(params(":listname"))
    save(list)
    
    val userlist = new UserList(getEmail, list.getId)
    save(userlist)
    
    redirect("/list/" + list.getId)
  }
  
  post("/delete/:listid") {
    val list = single(classOf[ShopList], params(":listid").toLong)
    val name = list.getName
    
    // Remove association
    remove(classOf[UserList], KeyFactory.createKey(classOf[UserList].getSimpleName, list.getId + getEmail))
    
    // The rest of the associations
    val userlists = query { q =>
      q.setClass(classOf[UserList])
      q.setFilter("listid == listParam")
      q.execute(list.getId)
    }
    
    // Remove the list if no association
    if(userlists.size == 0) {
      // Remove the items associated with the list
      val items: List[Item] = query { q =>
        q.setClass(classOf[Item])
        q.setFilter("listid == idParam")
        q.execute(list.getId)
      }
      items foreach(i => remove(classOf[Item], i.getId))
      remove(classOf[ShopList], list.getId)
    }
    
    name + " has been deleted."
  }
  
  get("/list/:listid") {
    val list = single(classOf[ShopList], params(":listid").toLong)
    
    val items = query { q =>
      q.setClass(classOf[Item])
      q.setFilter("listid == listidParam")
      q.setOrdering("id desc")
      q.execute(params(":listid").toLong)
    }
    
    template("list", ("list", list), ("item", items))
  }
  
  get("/lists") {
    val userlists = query { q =>
      q.setClass(classOf[UserList])
      q.setFilter("email == emailParam")
      q.execute(getEmail)
    }
    
    template("lists", ("lists", userlists))
  }
  
  post("/additem/:listid") {
	val item = new Item("", params(":listid").toLong)
    
    save(item)
    
    template("item", ("item", item))
  }
  
  post("/edititem/:id/:name/:quantity/:price") {
    val item = single(classOf[Item], params(":id").toLong)
    
    item.setName(params(":name"))
    item.setQuantity(params(":quantity").toInt)
    item.setPrice(params(":price").toDouble)
    save(item)
    
    "Success"
  }
  
  post("/deleteitem/:itemid") {
    remove(classOf[Item], params(":itemid").toLong)

    "Success"
  }
}
