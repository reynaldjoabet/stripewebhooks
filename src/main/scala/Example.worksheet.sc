import cats.implicits._

//import cats.syntax.all._
//cats.implicits seems to include the extension methods in cats.syntax.all plus the type class instances
//import cats.implicits._ brings in extension methods on any F[A]

//  implicit final def catsSyntaxMonad[F[_], A](fa: F[A]): MonadOps[F, A] = new MonadOps(fa)

val option1: Option[Int] = Some(42)
val option2: Option[Int] = none[Int]

// Using |+| (combine) syntax on Options
val combinedOptions: Option[Int] = option1 |+| option2

val list1: List[Int] = List(1, 2, 3)
val list2: List[Int] = List(4, 5, 6)

// Using Semigroup instance to combine lists
val combinedLists: List[Int] = list1 |+| list2

// Example 3: Using Applicative syntax
val maybeInt: Option[Int] = 42.pure[Option]
val maybeString: Option[String] = "hello".pure[Option]
