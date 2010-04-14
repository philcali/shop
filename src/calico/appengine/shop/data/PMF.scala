package calico.appengine.shop.data

import javax.jdo.JDOHelper
import javax.jdo.Query
import javax.jdo.PersistenceManager
import javax.jdo.JDOHelper

object PMF {
  private val pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional")
  
  def save(data: Any) {
    using { pm =>
      pm.makePersistent(data)
    }
  }

  def remove(clazz: java.lang.Class[_], id: Any) {
    using { pm => 
      val obj = pm.getObjectById(clazz, id)
      pm.deletePersistent(obj)
    }
  }
  
  def getSingle[T](clazz: java.lang.Class[T], id: Any) = {
    using(_.getObjectById(clazz, id))
  } 
  
  def using[T](fun: PersistenceManager => T) = {
    val pm = pmf.getPersistenceManager
    
    try {
      val results = fun(pm)
      results
    } finally {
      pm.close
    }
  }
  
  def query[T](q: Query => Any): List[T] = {
    using { pm =>
      val qu = pm.newQuery()
      
      try {
        val results = q(qu).asInstanceOf[java.util.List[T]]
        results
      } finally {
        qu.closeAll
      }
      
    }    
  }
  
  implicit def javalist2scalalist[T](list: java.util.List[T]): List[T] = {
    list.toArray(new Array[T](list.size)).toList
  }
}
