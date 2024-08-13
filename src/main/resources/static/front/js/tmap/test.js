window.addEventListener("DOMContentLoaded", function() {
   tmapLib.load("map");
   // 버튼 이벤트 리스너
   document.getElementById("reset").addEventListener("click", () => tmapLib.reset());
   document.getElementById("start").addEventListener("click", () => tmapLib.currentAction = 'start');
   document.getElementById("end").addEventListener("click", () => tmapLib.currentAction = 'end');
   document.getElementById("vias").addEventListener("click", () => tmapLib.currentAction = 'via');
   document.getElementById("complete").addEventListener("click", () => tmapLib.route(tmapLib.mapId));
   const resetEl = document.getElementById("reset");
   resetEl.addEventListener("click", function() {
      if (confirm("정말 다시 선택?")) {
         tmapLib.reset();
      }
   });

});