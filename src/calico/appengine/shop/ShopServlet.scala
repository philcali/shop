package calico.appengine.shop

import com.thinkminimo.step.Step
import calico.appengine.shop.templating.Templating
import com.google.appengine.api.users.UserServiceFactory
import calico.appengine.shop.data.PMF.{save, query, remove, getSingle => single, using}
import calico.appengine.shop.data.{ShopList, UserList, Item, ShareRequest}
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
    load("request", classOf[ShareRequest])
    
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

    load("list", "iterate") { obj =>
      obj.asInstanceOf[List[UserList]].foldLeft("") { (str, l) =>
        str + template("listrow", ("list", single(classOf[ShopList], l.getListId)))
      }
    }
    
    load("request", "id") { obj =>
      obj.asInstanceOf[ShareRequest].getKey.getName
    }
    
    load("request", "iterate") { (obj) => 
      obj.asInstanceOf[List[ShareRequest]].foldLeft("") { (str, l) =>
        str + template("requestrow", ("request", l))
      }
    }
    
    loadList(List("request", "list"), "date") { (p, obj) =>
      val sdf = new java.text.SimpleDateFormat("MM/dd/yyyy")
      sdf.format(obj.asInstanceOf[{def getCreatedAt: java.util.Date}].getCreatedAt)
    }
    
    load("request", "info") { obj =>
      val r = obj.asInstanceOf[ShareRequest]
      val list = single(classOf[ShopList], r.getListid)
      val rr = if(r.getRequester == getEmail) " you " else r.getRequester
      val re = if(r.getRequestee == getEmail) " you " else r.getRequestee
      list.getName + " from " + rr + " to " + re
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
      q.declareParameters("Long listidParam")
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
      q.declareParameters("Long listParam")
      q.execute(list.getId)
    }
    
    // Remove the list if no association
    if(userlists.size == 0) {
      // Remove the items associated with the list
      val items: List[Item] = query { q =>
        q.setClass(classOf[Item])
        q.setFilter("listid == idParam")
        q.declareParameters("Long idParam")
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
      q.declareParameters("Long listidParam")
      q.execute(params(":listid").toLong)
    }
    
    template("list", ("list", list), ("item", items))
  }
  
  get("/lists") {
    val userlists = query { q =>
      q.setClass(classOf[UserList])
      q.setFilter("email == emailParam")
      q.declareParameters("String emailParam")
      q.execute(getEmail)
    }
    
    template("lists", ("list", userlists))
  }
  
  get("/requests") {
    val pendingRequests = query { q =>
      q.setClass(classOf[ShareRequest])
      q.setFilter("requester == requestParam")
      q.declareParameters("String requestParam")
      q.execute(getEmail)
    }
    
    val otherRequests = query { q =>
      q.setClass(classOf[ShareRequest])
      q.setFilter("requestee == requestParam")
      q.declareParameters("String requestParam")
      q.execute(getEmail)
    }
    
    template("requests", ("request", otherRequests ::: pendingRequests))
  }
  
  post("/acceptlist/:key") {
    val request = single(classOf[ShareRequest], params(":key"))
    
    // if the requester accepts his own, just ignore
    if(request.getRequester == getEmail) "fail"
    else {
      val userlist = new UserList(getEmail, request.getListid)
      save(userlist)
      
      // Invalidate request now
      remove(classOf[ShareRequest], request.getKey)
      "success"
    }
  }
  
  post("/denyrequest/:key") {
    remove(classOf[ShareRequest], params(":key"))
    
    "success"
  }
  
  post("/sharelist/:listid/:user") {
    val request = new ShareRequest(params(":listid").toLong, getEmail, params(":user"))
    
    save(request)
    
    val list = single(classOf[ShopList], params(":listid").toLong)
    
    list.getName + " was shared with " + params(":user")
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
