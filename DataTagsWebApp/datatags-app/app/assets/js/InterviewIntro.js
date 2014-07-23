var InterviewIntro = function(){
  var buttonId="#startTaggingButton";
  var shadoxMax=40;
  var frameDelay=40;
  var frameCount=25;
  var curFrame=0;
  var done = function () {
    curFrame=0;
  }
  var nextFrame = function() {
    curFrame = curFrame+1;
    var val=Math.sqrt(curFrame/frameCount)*shadoxMax
    var spread = Math.round(val/1.5) + "px";
    var blur = Math.round(val) + "px";
    var shadow = "0 0 " + blur + " " + spread + " #FFF";
    // console.log( curFrame + " " + shadow );
    $(buttonId)[0].style.boxShadow=shadow;
    setTimeout( (curFrame==frameCount)? done: nextFrame, frameDelay );
  }
  return {
    highlightStartTaggingButton: function() {
      setTimeout(nextFrame, frameDelay);
    }
  };

}();