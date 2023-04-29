package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}


//Primero, se define la clase Tod, que representa un elemento de la lista de tareas y contiene un identificador único (id), un nombre (name)
// y una bandera que indica si la tarea ha sido completada (isComplete).
case class Todo(id: Long, name: String, isComplete: Boolean)

// se define la clase TodoFormData, que se utiliza para validar y procesar los datos del formulario HTML que se utiliza
// para agregar o editar una tarea. Esta clase contiene los mismos campos que la clase Tod, pero no tiene un identificador único
//  ya que se utiliza solo para crear o actualizar tareas.
case class TodoFormData(name: String, isComplete: Boolean)


//A continuación, se define el objeto TodoForm, que se utiliza para validar y procesar los datos del formulario HTML.
// Este objeto utiliza la clase Form de Play Framework y define un formulario que contiene dos campos:
// name, que es un campo de texto obligatorio (nonEmptyText), y isComplete, que es un campo booleano.
object TodoForm {
  val form: Form[TodoFormData] = Form(
    mapping(
      "name" -> nonEmptyText,
      "isComplete" -> boolean
    )(TodoFormData.apply)(TodoFormData.unapply)
  )
}

//Luego, se define la clase TodoTableDef, que representa la definición de la tabla de base de datos que contiene los datos de la lista de tareas.
// Esta clase define los campos de la tabla (id, name, isComplete) y los tipos de datos que se utilizan para almacenar los datos en la base de datos.
// Además, define un método * que se utiliza para convertir los resultados de las consultas de la base de datos en objetos de la clase Todo.
class TodoTableDef(tag: Tag) extends Table[Todo](tag, "todo") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def isComplete = column[Boolean]("isComplete")

  override def * = (id, name, isComplete) <> (Todo.tupled, Todo.unapply)
}


//Por último, se define la clase TodoList, que se utiliza para interactuar con la base de datos y realizar operaciones CRUD
// (Crear, Leer, Actualizar, Borrar) en los datos de la lista de tareas. Esta clase utiliza la inyección de dependencias (@Inject)
// para obtener una instancia del proveedor de configuración de base de datos (dbConfigProvider) y define los métodos
// add, delete, update, get y listAll para realizar las operaciones CRUD en la base de datos. Cada uno de estos métodos utiliza
// el objeto dbConfig para acceder a la base de datos y ejecutar las consultas necesarias.
class TodoList @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider
                        )(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val todoList = TableQuery[TodoTableDef]

  def add(todoItem: Todo): Future[String] = {
    dbConfig.db
      .run(todoList += todoItem)
      .map(_ => "TodoItem successfully added")
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(todoList.filter(_.id === id).delete)
  }

  def update(todoItem: Todo): Future[Int] = {
    dbConfig.db
      .run(todoList.filter(_.id === todoItem.id)
        .map(x => (x.name, x.isComplete))
        .update(todoItem.name, todoItem.isComplete)
      )
  }

  def get(id: Long): Future[Option[Todo]] = {
    dbConfig.db.run(todoList.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Todo]] = {
    dbConfig.db.run(todoList.result)
  }
}
