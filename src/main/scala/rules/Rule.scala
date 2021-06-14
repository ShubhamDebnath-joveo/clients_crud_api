package rules

import model.Job

import java.lang.reflect.Field

abstract class Rule() {
  def evaluate(job: Job): Boolean
}

case class RuleGroup(operator: String, rules: List[Rule]) extends Rule {
  override def evaluate(job: Job): Boolean = {
    val innerArgs = rules.map(rule => rule.evaluate(job))

    operator match {
      case "AND" => innerArgs.foldLeft[Boolean](true)(_ & _)
      case "OR" => innerArgs.foldLeft[Boolean](true)(_ | _)
      case _ =>
        println(s"Operator $operator not supported")
        false
    }
  }
}

case class RuleElement(operator: String, field: String, data: Any, fieldType: String) extends Rule {
  override def evaluate(job: Job): Boolean = {

    fieldType match {
      case "String" => evaluateOp[String](job)
      case "Int" => evaluateOp[Int](job)
      case "Double" => evaluateOp[Double](job)
      case _ =>
        println(s"Data type $fieldType not supported")
        false
    }
  }

  private def evaluateOp[T](job: Job): Boolean = {

    operator match {
      case "EQUAL" => data.asInstanceOf[T] == getFieldValue[T](job)
      case "IN" => data.asInstanceOf[List[T]].contains(getFieldValue[T](job))
      case "STARTS_WITH" => getFieldValue[String](job).matches(s"^${data.asInstanceOf[String]}.*")
      case _ =>
        println(s"Operator $operator not supported")
        false
    }
  }

  private def getFieldValue[T](job: Job): T = {
    val declaredField: Field = job.getClass.getDeclaredField(field)
    declaredField.setAccessible(true)
    declaredField.get(job).asInstanceOf[T]
  }
}
