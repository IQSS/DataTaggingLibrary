package util

import edu.harvard.iq.datatags.model.types._
import edu.harvard.iq.datatags.model.values._
import edu.harvard.iq.datatags.model.values.TagValue

import play.api.libs.json._
import play.api.libs.json.Json


import scala.collection.JavaConversions._


object Jsonizer extends TagValue.Visitor[JsValue]{

	def visitToDoValue (todo: ToDoValue) = {
		Json.toJson(todo.getName)
	}

	def visitSimpleValue (simple: SimpleValue) = {
		Json.toJson(simple.getName)
	}

	def visitAggregateValue (aggregate: AggregateValue) = {
		var scalaSet: scala.collection.mutable.Set[SimpleValue] = aggregate.getValues
		Json.toJson(scalaSet.map( (value:SimpleValue) => visitSimpleValue(value))) // visitSimpleValue(_)
	}

	def visitCompoundValue (compound: CompoundValue) = {
		var compoundMap = collection.mutable.Map[String, JsValue]()
		for (fieldType <- compound.getSetFieldTypes) {
			compoundMap += (fieldType.getName -> compound.get(fieldType).accept(this))
		}
		val compoundSeq = compoundMap.toSeq
		JsObject(compoundSeq)
	}

}
