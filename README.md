# stripewebhooks
Sample application to illustrate making use of stripe webhooks 


using Cats 2.2.0 and above:

import cats._, cats.data._, cats.syntax.all._

Prior to Cats 2.2.0 it was:

import cats._, cats.data._, cats.implicits._

In Scala, imports are used for two purposes:

- To include names of values and types into the scope.
- To include implicits into the scope.

Given some type A, implicit is a mechanism to ask the compiler for a specific (term) value for the type. This can be used for different purposes, for Cats, the 2 main usages are:

instances; to provide typeclass instances.
syntax; to inject methods and operators. (method extension)


Make a post request to stripe
intent to pay-create payment
The stripe adds a pending charge
Strip creates a pending payment intent

Stripe response contains a client-secret which is meant to be sent to the client frontend





# Session

A Checkout Session represents your customer’s session as they pay for one-time purchases or subscriptions through Checkout or Payment Links. We recommend creating a new Session each time your customer attempts to pay.

Once payment is successful, the Checkout Session will contain a reference to the Customer, and either the successful PaymentIntent or an active Subscription.

You can create a Checkout Session on your server and redirect to its URL to begin Checkout.


POST
/v1/checkout/sessions
GET
/v1/checkout/sessions/:id
GET
/v1/checkout/sessions/:id/line_items
GET
/v1/checkout/sessions
POST
/v1/checkout/sessions/:id/expire

[cats](https://eed3si9n.com/herding-cats/import-guide.html)