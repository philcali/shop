package calico.appengine.shop.templating

import scala.collection.mutable.{Map => MMap, ListBuffer => MList}
import scala.io.Source.{fromFile => open}
import java.io.File
import utils.FileUtils.recurse

trait Templating {
  val paths = MMap[String, String]()
  val beforeTemplate = new MList[String]()
  val afterTemplate = new MList[String]()
  val definitions = MMap[String, MMap[String, java.lang.Object => Any]]()
  
  def loadTemplates(path: String) {
    recurse(path) { f =>
      if(f.getName.endsWith(".schtml")) {
        val name = f.getName.split("\\.")(0)
        paths put(name, f.toURL.getPath)
        
        load("template", name) { obj =>
          val data = obj.asInstanceOf[Array[(String, java.lang.Object)]]
          template(name, data:_*)
        }
      }
    }
  }
  
  def load(identity: String, obj: java.lang.Class[_]) {
    val fields = obj.getDeclaredFields

    if (!definitions.contains(identity)) {
      val fs = MMap() ++ (for(f <- fields; if(!f.getName.startsWith("jdo"))) yield {
        f.setAccessible(true)
        (f.getName, (x: java.lang.Object) => 
         f.get(x).asInstanceOf[Any]
        )
      }).toList
      definitions.put(identity, fs)
    }
  }
  
  def load(identity: String, field: String) (fun: java.lang.Object => Any) {
    if (!definitions.contains(identity)) {
      val sure = MMap(field -> fun)
      definitions.put(identity, sure)
    } else {
      definitions(identity).put(field, fun)
    }
  }
  
  def loadUniversal(field: String) (fun: (String, java.lang.Object) => Any) {
    definitions.keys foreach(id => load(id, field)(fun(id, _)))
  }
  
  def loadList(ids: List[String], field: String)(fun: (String, java.lang.Object) => Any) {
    ids foreach(id => load(id, field)(fun(id, _)))
  }
  
  def template(path: String, data: (String, java.lang.Object)*) = {
    val file = open(paths(path))
    var contents = file.getLines.mkString
    
    val escapeDollar = """\$""".r
    
    for(e <- data; val(id, obj) = e) {
      if (definitions contains id) {
        for((field, fun) <- definitions(id)){
          val reg = ("\\{:"+ id + "\\." + field + "}").r
          if(reg.findFirstIn(contents) != None) {
        	
            contents = reg.replaceAllIn(contents, escapeDollar.replaceAllIn(fun(obj).toString, "\\\\\\$"))
          }
        }
      }
    }
    
    contents
  }
  
  def templateWrap(beforeData: (String, java.lang.Object)*) (block: => String) = {
    var contents = beforeTemplate.foldLeft(""){(str, path) => template(path, beforeData:_*)}
    contents += block
    contents += afterTemplate.foldLeft(""){(str, path) => template(path)}
    contents
  }
}
