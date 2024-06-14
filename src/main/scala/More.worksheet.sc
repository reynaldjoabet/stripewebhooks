import java.util.UUID

import cats.Show

Show[Int].show(42)
//could not find implicit value for parameter instance: cats.Show[Int]

//import cats.implicits._
//adding the above import made it work in cats2.1
// In 2.1.x instances of type classes were defined in separate objects so in order to be in scope (local scope) they had to be imported

// object implicits extends instances.AllInstances with ...

// trait AllInstances extends AnyValInstances with ...

// trait AnyValInstances extends IntInstances with ...

// trait IntInstances extends cats.kernel.instances.IntInstances {
//   implicit val catsStdShowForInt: Show[Int] = Show.fromToString[Int]
// }

// In 2.2.0 instances of type classes are defined in companion objects so they are in scope (implicit scope) automatically and do not have to be imported

// object Show extends ScalaVersionSpecificShowInstances with ShowInstances {
//   ...
//   implicit def catsShowForInt: Show[Int] = cats.instances.int.catsStdShowForInt
//   ...
// }
Show[Int].show(42)

import cats.Monad

Monad[List]

import cats.instances.future
import cats.instances.order
import cats.MonadError

MonadError

Show[UUID].show(UUID.randomUUID())

case class Person(name: String, age: Int)

val person = Person("John", 12)

val Person(name, age) = person

"CACF51660E6BF90F4E63B8A45BADF2756D9D9160E7FC926A6FB9A641B068A99E-1708603042997-071242F4C84B06FA522B6F84D9D36710F5E16BF7"
  .length()
