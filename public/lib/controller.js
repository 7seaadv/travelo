
app.controller('ApplicationController', function(){
	
});

app.controller('ProfileController', function($scope, $http, $timeout){
	
	$scope.getUserProfile = function(){
		$http.get('/getUserProfile',{}).then(function(res){
			$scope.user = res.data;
			if($scope.user.dateOfBirth != null){
				$scope.user.dateOfBirth = moment($scope.user.dateOfBirth).format("YYYY-MM-DD");
			}
			if($scope.user.profilePhotoId != null){
				$scope.profilePhoto = "/getPhotoById/"+$scope.user.profilePhotoId+"/ProfilePhoto";
			} else {
				$scope.profilePhoto = "assets/images/default_user.png";
			}
		}, function(error){
			console.log(error.data);
		});
	}	
	
});	
	
app.controller('EditProfileController', function($scope, $http, $timeout, $upload){
	
	$scope.getUserProfile = function(){
		$http.get('/getUserProfile',{}).then(function(res){
			$scope.user = res.data;
			if($scope.user.dateOfBirth != null){
				$scope.user.dateOfBirth = moment($scope.user.dateOfBirth).format("YYYY-MM-DD");
			}
			if($scope.user.profilePhotoId != null){
				$scope.profilePhoto = "/getPhotoById/"+$scope.user.profilePhotoId+"/ProfilePhoto";
			} else {
				$scope.profilePhoto = "assets/images/default_user.png";
			}
		}, function(error){
			console.log(error.data);
		});
	}
	
	$scope.places = [];
	$scope.showDropdown = true;
	$scope.getGooglePlaces = function(){
		if($scope.searchText && $scope.searchText != ""){
			$http.get('/getGooglePlaces/'+$scope.searchText,{}).then(function(res){
				$scope.places = res.data.predictions;
			}, function(error){
				console.log(error.data);
			});
		} else {
			$scope.places = [];
		}
	}
	
	$scope.getPlaceDetails = function(p){
		$http.get('/getPlaceDetails/'+p.place_id,{}).then(function(res){
			$scope.place = {
					name: res.data.result.name,
					description: res.data.result.formatted_address,
					latLng: [res.data.result.geometry.location.lat,res.data.result.geometry.location.lng]
			}
			$scope.user.placesBeenTos.push($scope.place);
			$scope.searchText = "";
		}, function(error){
			console.log(error.data);
		});
	}
	
	$scope.closeDropdown = function(){
		$timeout(function(){
			$scope.showDropdown = false;
		}, 500);
	}
	
	var file = "";
	$scope.onFileSelect = function ($files) {
		file = $files[0];
		for (var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function (fileReader, index) {
					fileReader.onload = function (e) {
						$timeout(function () {
							$scope.profilePhoto = e.target.result;
						});
					}
				}(fileReader, i);
			}
		}
	}
	
	$scope.updateUserProfile = function(){
		if(file != ""){
			$scope.upload = $upload.upload({
				url: '/uploadPhoto',
				method: 'post',
				fileFormDataName: 'image',
				file: file
			}).success(function (res) {
				$scope.user.profilePhotoId = res.id;
				$http.post('/updateUserProfile',{"user":$scope.user}).then(function(res){
					$scope.getUserProfile();
					file = "";
				}, function(error){
					console.log(error.data);
				});
			});
		} else {
			$http.post('/updateUserProfile',{"user":$scope.user}).then(function(res){
				$scope.getUserProfile();
			}, function(error){
				console.log(error.data);
			});
		}
	}
	
});