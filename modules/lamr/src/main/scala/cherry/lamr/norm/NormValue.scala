package cherry.lamr.norm

import cherry.lamr.{BuiltinType, Lang, LibRef, RecordKey}
import cherry.fix.Fix
import tofu.syntax.*
import cats.syntax.show.*
import cherry.utils.{Act, ErrorCtx}
import cherry.lamr.norm.umami.{
  BooleanValue,
  BuiltinNormType,
  FloatValue,
  IntegerValue,
  Narrow,
  NormType,
  StringValue,
  UnitValue
}

type Term = Fix[Lang]
trait Normalizer:
  def normalize(term: Term, context: NormValue): Process[NormValue]

trait NormValue:
  given ErrorCtx[State] = _.value = Some(this)

  def toTerm: Term

  def view(context: NormValue): Term = toTerm

  def headNorm: Process[NormValue] = Act.pure(this)

  def errorDisplay: Option[String] = Some(toTerm.toString)

  def position: Option[Position] = None

  def isAbstract: Boolean = false

  def apply(term: NormValue): Process[NormValue] = Act.error(Cause.BadType(TypeCause.Function))

  def get(key: RecordKey, up: Int): Process[NormValue] =
    Act.error(Cause.BadType(TypeCause.Record))

  def asType: Process[NormType] = Act.error(Cause.BadType(TypeCause.Type))

  def merge(term: NormValue): Process[NormValue] = Act.pure(umami.Merge(this, term))

  def narrow(domain: NormType): Process[NormValue] =
    if domain == BuiltinNormType(BuiltinType.Any) then UnitValue.pure
    else Act.error(Cause.UnrelatedValue(domain))

  def first = get(0, 0)

  def second = get(1, 0)

  def isUnit: Boolean = false

  def asInt: Process[BigInt] = this match
    case IntegerValue(v) => Act.pure(v)
    case _               => Act.error(Cause.BadType(TypeCause.Builtin(BuiltinType.Integer)))

  def asDouble: Process[Double] = this match
    case FloatValue(v) => Act.pure(v)
    case _             => Act.error(Cause.BadType(TypeCause.Builtin(BuiltinType.Float)))

  def asBool: Process[Boolean] = this match
    case BooleanValue(v) => Act.pure(v)
    case _               => Act.error(Cause.BadType(TypeCause.Builtin(BuiltinType.Bool)))

  def asStr: Process[String] = this match
    case StringValue(v) => Act.pure(v)
    case _              => Act.error(Cause.BadType(TypeCause.Builtin(BuiltinType.Str)))
