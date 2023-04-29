package controllers


import models.{Todo, TodoForm}
import play.api.libs.json._
import play.api.mvc._
import service.TodoService

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


//En la primera línea se define la clase TodoController que extiende de AbstractController y tiene dos parámetros:
// cc que es de tipo ControllerComponents y todoService que es de tipo TodoService. La anotación @Inject() indica
// que la instancia de la clase será gestionada por un contenedor de inyección de dependencias.
class TodoController @Inject()(cc: ControllerComponents, todoService: TodoService) extends AbstractController(cc) {

  //Aquí se define un formato de serialización/deserialización para la clase Tod utilizando la librería de serialización JSON de Play.
  implicit val todoFormat: OFormat[Todo] = Json.format[Todo]

  //Este método define una acción que devuelve todos los elementos de la lista de tareas almacenada en la base de datos.
  // La acción es asíncrona y devuelve un resultado de tipo Action[AnyContent].
  // La lista de elementos se obtiene mediante una llamada al método listAllItems del servicio todoService y se serializa como JSON antes de ser enviada en la respuesta.
  def getAll: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    todoService.listAllItems map { items =>
      Ok(Json.toJson(items))
    }
  }

  //Este método define una acción que devuelve un elemento específico de la lista de tareas almacenada en la base de datos.
  // La acción es asíncrona y devuelve un resultado de tipo Action[AnyContent].
  // El elemento se obtiene mediante una llamada al método getItem del servicio todoService y se serializa como JSON antes de ser enviada en la respuesta.
  def getById(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    todoService.getItem(id) map { item =>
      Ok(Json.toJson(item))
    }
  }


  //Este método define una acción que agrega un nuevo elemento a la lista de tareas almacenada en la base de datos.
  // La acción es asíncrona y devuelve un resultado de tipo Action[AnyContent].
  // El nuevo elemento se obtiene de los datos recibidos en la solicitud y se almacena mediante una llamada al método addItem del servicio todoService.
  // Si la solicitud no puede procesarse correctamente, se devuelve un error BadRequest.
  def add(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    TodoForm.form.bindFromRequest.fold(
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val newTodoItem = Todo(0, data.name, data.isComplete)
        todoService.addItem(newTodoItem).map(_ => Redirect(routes.TodoController.getAll))
      })
  }


  //La acción update utiliza un objeto de formulario TodoForm para vincular los datos de la solicitud con un objeto de datos data.
  // Si el formulario contiene errores, se devuelve una respuesta BadRequest. De lo contrario, se crea un objeto Tod con los datos del formulario
  // y se utiliza un servicio todoService para actualizar el elemento. Luego, se redirige al usuario a la lista de todos los elementos con la acción
  // Redirect(routes.TodoController.getAll).
  def update(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    TodoForm.form.bindFromRequest.fold(
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val todoItem = Todo(id, data.name, data.isComplete)
        todoService.updateItem(todoItem).map(_ => Redirect(routes.TodoController.getAll))
      })
  }


  //La acción delete utiliza un servicio todoService para eliminar el elemento con el identificador dado.
  // Luego, se redirige al usuario a la lista de todos los elementos con la acción Redirect(routes.TodoController.getAll).
  def delete(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    todoService.deleteItem(id) map { res =>
      Redirect(routes.TodoController.getAll)
    }
  }
}
