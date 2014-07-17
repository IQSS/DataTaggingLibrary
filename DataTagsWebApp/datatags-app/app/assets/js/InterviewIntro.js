var InterviewIntro = function(){
  var buttonId="#startTaggingButton";
  var duration=750;
  var done = function () {
    $(buttonId).animate( {fontSize:"18px", top:"0px"}, duration );
  }
  return {
    highlightStartTaggingButton: function() {
      endSize = $(buttonId)[0].style.fontSize;
      $(buttonId).animate( {fontSize:"28px", top:"-5px"}, duration, done );
    }
  };

}();