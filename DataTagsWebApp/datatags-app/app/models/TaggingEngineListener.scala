package models

import edu.harvard.iq.datatags.runtime.RuntimeEngine
import edu.harvard.iq.datatags.model.charts.nodes.Node
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException

/**
 * Lists the engine run history
 */
class TaggingEngineListener extends edu.harvard.iq.datatags.runtime.RuntimeEngine.Listener {

  var exception:DataTagsRuntimeException = null
  private val _traversedNodes = collection.mutable.Buffer[Node]()

  override def runStarted(p1: RuntimeEngine): Unit = {}

  override def runTerminated(p1: RuntimeEngine): Unit = {}

  override def runError(p1: RuntimeEngine, anException: DataTagsRuntimeException): Unit = {
    exception = anException
  }

  override def processedNode(p1: RuntimeEngine, aNode: Node): Unit = {
    _traversedNodes += aNode
  }

  def traversedNodes = _traversedNodes.toSeq
}
