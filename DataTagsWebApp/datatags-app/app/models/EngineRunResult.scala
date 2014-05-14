package models

import edu.harvard.iq.datatags.runtime.RuntimeEngineState
import edu.harvard.iq.datatags.model.charts.nodes._
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException


case class EngineRunResult( state: RuntimeEngineState,
                        traversed: Seq[Node],
                            error: DataTagsRuntimeException
) 