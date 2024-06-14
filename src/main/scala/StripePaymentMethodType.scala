import io.circe.Decoder
sealed abstract class StripePaymentMethodType

object StripePaymentMethodType {

  case object alipay     extends StripePaymentMethodType
  case object wechat_pay extends StripePaymentMethodType
  case object promptpay  extends StripePaymentMethodType
  case object pix        extends StripePaymentMethodType
  case object card       extends StripePaymentMethodType
  case object cashapp    extends StripePaymentMethodType
  case object grabpay    extends StripePaymentMethodType
  case object paynow     extends StripePaymentMethodType
  case object paypal     extends StripePaymentMethodType

  implicit val stripePaymentMethodTypeDecoder: Decoder[StripePaymentMethodType] = Decoder[String]
    .emap[StripePaymentMethodType] {
      case "alipay"     => Right(alipay)
      case "promptpay"  => Right(promptpay)
      case "pix"        => Right(pix)
      case "paypal"     => Right(paypal)
      case "paynow"     => Right(paynow)
      case "grabpay"    => Right(grabpay)
      case "cashapp"    => Right(cashapp)
      case "card"       => Right(card)
      case "wechat_pay" => Right(wechat_pay)

    }

}
