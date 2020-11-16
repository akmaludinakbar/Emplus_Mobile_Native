angular.module('app.controllers', ['pdf'])

//setting untuk merubah JSON to $_POST-----------
.config(function ($httpProvider, $ionicConfigProvider, $controllerProvider) {
  //for global functioning
  $controllerProvider.allowGlobals();

  //to format POST data into Json filetype
  var serialize = function(obj, prefix) {
    var str = [];
    for(var p in obj) {
      if (obj.hasOwnProperty(p)) {
        var k = prefix ? prefix + "[" + p + "]" : p, v = obj[p];
        str.push(typeof v == "object" ?
          serialize(v, k) :
          encodeURIComponent(k) + "=" + encodeURIComponent(v));
      }
    }
    return str.join("&");
  };

  // send all requests payload as query string
  $httpProvider.defaults.transformRequest = function(data){
      if (data === undefined) {
          return data;
      }
      return serialize(data);
  };

  //set request timeout
  $httpProvider.interceptors.push('timeoutHttpIntercept');

  // set all post requests content type
  $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

  //set tabs-view on the bottom of the page
  $ionicConfigProvider.tabs.position('bottom'); // other values: top

})
//------------------------------------------

.run(function($ionicPlatform, $ionicPopup) {

})
