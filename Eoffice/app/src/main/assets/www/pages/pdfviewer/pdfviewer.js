angular.module('app.controllers')

.controller('pdfviewerCtrl', ['$cordovaDevice','$scope', '$stateParams', '$timeout', '$state', '$ionicLoading', '$http', '$ionicPopup',
// You can include any angular dependencies as parameters for this function
// TIP: Access Route Parameters for your page via $stateParams.parameterName
function ($cordovaDevice, $scope, $stateParams, $timeout, $state, $ionicLoading, $http, $ionicPopup) {
  $scope.pageNum = "-";
  $scope.pageCount = "-";
  $scope.pdfName = "";

  var query = decodeURIComponent(document.URL);
  var vars = query.split("#");
  console.log(vars);

  $scope.id_pdf = vars[2].toString();
  $scope.versi = vars[3].toString();
  $scope.api_key = vars[5].toString();
  $scope.url = vars[6].toString();
  console.log($scope.url);

    

  $ionicLoading.show();

  function base64ToArrayBuffer(base64) {
        var binaryString =  window.atob(base64);
        var binaryLen = binaryString.length;
        var bytes = new Uint8Array(binaryLen);
        for (var i = 0; i < binaryLen; i++)        {
            var ascii = binaryString.charCodeAt(i);
            bytes[i] = ascii;
        }
        return bytes;
  }

  var saveByteArray = (function () {
    return function (data, name) {
        var blob = new Blob(data, {type: "application/pdf"}),

            url = window.URL.createObjectURL(blob);

            console.log(url);

            loadPdf(url);
    };
  }());

    //var api_key = "e91b0e31f9c9ce12e6c952673c53e2ebfe729d53af7156736a8f42e3cb7bf145";

  var input = {
        a:$scope.api_key,
        id:$scope.id_pdf,
        av:$scope.versi
   }
  $http.post($scope.url,input)
      .success(function(response){
        console.log(response);

        $ionicLoading.hide();

		$scope.pdfName = response.NamaFile;

		//for streaming file
		var sampleBytes = base64ToArrayBuffer(response.NilaiBytes);
		// var sampleBytes = base64ToArrayBuffer(response); //debug
		saveByteArray([sampleBytes], 'readme.pdf');

		//applying Watermark
		$scope.npp = vars[4].toString();
		console.log($scope.npp);
		//for local file
		// var currentBlob = new Blob([response], {type: 'application/pdf'});
		// loadPdf(URL.createObjectURL(currentBlob));

		if (response.isWatermark == undefined || response.isWatermark == null || response.isWatermark == true) {
			loadWatermark();
		} else {
			console.log("Watermark non aktif");
		}

    }).error(function(response){
          $ionicLoading.hide();

          var alertPopup = $ionicPopup.alert({
              title: 'Proses gagal!',
              template: '<center>Terjadi gangguan dalam mengunduh dokumen.</center>'
          });
          console.log(response);
      })

      $scope.getNavStyle = function(scroll) {
        if(scroll > 100) return 'pdf-controls fixed';
        else return 'pdf-controls';
      }

  function loadPdf(url){
      //url = url.replace('blob:','');
      console.log(url);
      $scope.pdfUrl = url;
      $scope.pdfPassword = 'test';
      $scope.scroll = 0;
      $scope.loading = 'loading';

      $scope.onError = function(error) {
        console.log(error);
      }

      $scope.onLoad = function() {
        document.getElementById('loading').style.display = "none";

        window.URL.revokeObjectURL(url);
      }

      $scope.onProgress = function (progressData) {
          $scope.loading = "Loading pdf " + ((progressData.Loaded / progressData.total) * 100) + " %";
          console.log(progressData);
      };

      $scope.onPassword = function (updatePasswordFn, passwordResponse) {
        if (passwordResponse === PDFJS.PasswordResponses.NEED_PASSWORD) {
            updatePasswordFn($scope.pdfPassword);
        } else if (passwordResponse === PDFJS.PasswordResponses.INCORRECT_PASSWORD) {
            console.log('Incorrect password')
        }
      };
  }
  
  function loadWatermark() {
	var page = document.getElementById("watermark");
	var pageHeight = page.offsetHeight;
	var fontSize = 24;
	var lines = (parseInt(pageHeight) / fontSize);
	var totalWatermark = ((lines * 6) - ((lines * 6) % 6));
	var currentRow = 0;
	var currentCol = 0;

	//create row
	function createRow(indexOfRow) {
		var row = document.createElement('div');
		row.id = 'rows-' + indexOfRow;
		row.className = 'row no-padding no-margin';
		page.appendChild(row);
	}

	//create col
	function createCol(indexOfRow, indexOfCol) {
		var itsRow = document.getElementById('rows-' + indexOfRow);
		var col = document.createElement('div');
		col.id = 'cols-' + indexOfCol;
		col.className = 'col col-16 no-padding no-margin';
		itsRow.appendChild(col);
	}

	function writeWaterMark(indexOfCol, waterMark) {
		// console.log(watermark)
		var itsCol = document.getElementById('cols-' + indexOfCol);
		itsCol.innerHTML = "<span style='font-size: 5vw;color: rgba(0,0,0,0.07);'>" + waterMark + "</span>";
	}

	//create lines
	// console.log("lines", lines);
	for (var i = 0; i < totalWatermark; i++) {
		// console.log("Line", i);
		if (currentCol % 6 == 0) {
			console.log("Line", i, "CreateCol")
			currentRow++;
			createRow(currentRow);
		}
		currentCol++;
		createCol(currentRow, currentCol);
		if (currentRow % 2 != 0) {
			//row ganjil 
			if (currentCol % 2 != 0) {
				//col ganjil
				writeWaterMark(currentCol, $scope.npp.toString());
			} else {
				//col genap
				writeWaterMark(currentCol, "&nbsp;");
			}
		} else {
			//row genap
			if (currentCol % 2 != 0) {
				//col ganjil
				writeWaterMark(currentCol, "&nbsp;");
			} else {
				//col genap
				writeWaterMark(currentCol, $scope.npp.toString());
			}
		}
	}
}

}])
