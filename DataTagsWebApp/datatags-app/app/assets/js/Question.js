var Question = function() {
  return {
    revisit: function( nodeId ) {
        $.ajax( jsRoutes.controllers.Interview.revisit(nodeId) )
         .done( function(){
          window.location = jsRoutes.controllers.Interview.askNode(nodeId).absoluteURL();
         } );
    }
  }
}()

