package service

import com.google.inject.Inject
import models.{Todo, TodoList}

import scala.concurrent.Future

class TodoService @Inject()(items: TodoList) {


  //addItem(item: Tod): Future[String]: Este método recibe una tarea de tipo Tod y agrega esa tarea a la lista de tareas utilizando la instancia de TodoList.
  // Retorna un objeto Future que se completará con un String que indica si la operación fue exitosa
  def addItem(item: Todo): Future[String] = {
    items.add(item)
  }


  //deleteItem(id: Long): Future[Int]: Este método recibe el ID de una tarea y la elimina de la lista de tareas utilizando la instancia de TodoList.
  // Retorna un objeto Future que se completará con un Int que indica cuántas tareas fueron eliminadas.
  def deleteItem(id: Long): Future[Int] = {
    items.delete(id)
  }

  //updateItem(item: Tod): Future[Int]: Este método recibe una tarea de tipo Tod y actualiza esa tarea en la lista de tareas utilizando la instancia de TodoList.
  // Retorna un objeto Future que se completará con un Int que indica cuántas tareas fueron actualizadas
  def updateItem(item: Todo): Future[Int] = {
    items.update(item)
  }

  //getItem(id: Long): Future[Option[Tod]]: Este método recibe el ID de una tarea y devuelve esa tarea si está presente en la lista de tareas utilizando la instancia de TodoList.
  // Retorna un objeto Future que se completará con un Option que contiene la tarea correspondiente si está presente, o None si no lo está.
  def getItem(id: Long): Future[Option[Todo]] = {
    items.get(id)
  }

  //listAllItems: Future[Seq[Tod]]: Este método devuelve una lista de todas las tareas presentes en la lista de tareas utilizando la instancia de TodoList.
  // Retorna un objeto Future que se completará con una secuencia de objetos Tod.
  def listAllItems: Future[Seq[Todo]] = {
    items.listAll
  }
}
