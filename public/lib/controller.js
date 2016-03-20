
app.controller('ApplicationController', function($scope, UserServiceFactory){
	
	UserServiceFactory.getService().getUserBasicInfo().then(function(res){
		$scope.userBasicInfo = res.data;
	}, function(error){
		toastr.error("Error!");
	});
	
});

app.controller('ProfileController', function($scope, UserServiceFactory){
	
	$scope.getUserProfile = function(){
		UserServiceFactory.getService().getCurrentUserProfile().then(function(res){
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
			toastr.error("Error!");
		});
	}	
	
});	
	
app.controller('EditProfileController', function($scope, $timeout, $upload, $state, UserServiceFactory, UtilServiceFactory, ImageUploadServiceFactory){
	
	$scope.getUserProfile = function(){
		UserServiceFactory.getService().getCurrentUserProfile().then(function(res){
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
			toastr.error("Error!");
		});
	}	
	
	$scope.places = [];
	$scope.showDropdown = true;
	$scope.getGooglePlaces = function(){
		UtilServiceFactory.getService().getGooglePlaces($scope.searchText).then(function(res){
			$scope.places = res.data.predictions;
		}, function(error){
			toastr.error("Error!");
		});
	}
	
	$scope.closeDropdown = function(){
		$timeout(function(){
			$scope.showDropdown = false;
		}, 500);
	}
	
	$scope.getPlaceDetails = function(p){
		UtilServiceFactory.getService().getGooglePlaceDetails(p.place_id).then(function(res){
			$scope.place = {
				name: res.data.result.name,
				description: res.data.result.formatted_address,
				lngLat: [res.data.result.geometry.location.lng,res.data.result.geometry.location.lat]
			}
			$scope.user.placesBeenTos.push($scope.place);
			$scope.searchText = "";
		}, function(error){
			toastr.error("Error!");
		});
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
		ImageUploadServiceFactory.getService().uploadImage(file,$scope.user.profilePhotoId).then(function(res){
			$scope.user.profilePhotoId = res.id;
			UserServiceFactory.getService().updateCurrentUserProfile($scope.user).then(function(response){
				$scope.getUserProfile();
				file = "";
				toastr.success("Success!");
				if(res.id != null){
					$scope.userBasicInfo.profilePhotoId = res.id;
				}
				$scope.userBasicInfo.screenName = $scope.user.firstName + " " + $scope.user.lastName;
				$state.go("profile");
			}, function(error){
				toastr.error("Error!");
			});
		}, function(error){
			toastr.error("Error!");
		});
	}
	
});

app.controller('FindAnExpertController', function($scope, $timeout, $modal, UserServiceFactory, UtilServiceFactory){
	
	$scope.filter = {
		places: []
	}
	
	$scope.places = [];
	$scope.showDropdown = true;
	$scope.getGooglePlaces = function(){
		UtilServiceFactory.getService().getGooglePlaces($scope.searchText).then(function(res){
			$scope.places = res.data.predictions;
		}, function(error){
			toastr.error("Error!");
		});
	}
	
	$scope.closeDropdown = function(){
		$timeout(function(){
			$scope.showDropdown = false;
		}, 500);
	}
	
	$scope.getPlaceDetails = function(p){
		UtilServiceFactory.getService().getGooglePlaceDetails(p.place_id).then(function(res){
			$scope.place = {
				name: res.data.result.name,
				lngLat: [res.data.result.geometry.location.lng,res.data.result.geometry.location.lat]
			}
			$scope.filter.places.push($scope.place);
			$scope.searchText = "";
		}, function(error){
			toastr.error("Error!");
		});
	}
	
	$scope.removePlace = function(index){
		$scope.filter.places.splice(index,1);
	}
	
	$scope.experts = [];
	$scope.searchExperts = function(){
		UserServiceFactory.getService().searchExperts($scope.filter).then(function(res){
			$scope.experts = res.data;
			angular.forEach($scope.experts, function(item){
				if(item.profilePhotoId != null){
					item.profilePhoto = "/getPhotoById/"+item.profilePhotoId+"/ProfilePhoto";
				} else {
					item.profilePhoto = "assets/images/default_user.png";
				}
			});
		}, function(error){
			toastr.error("Error!");
		});
	}
	
	$scope.openSendRequestMessage = function(user){
		$scope.message = {
			toUserId: user.id,
			toUserName: user.firstName + " " + user.lastName,
			content: ""
		}
		var modalInstance = $modal.open({
            templateUrl: 'assets/views/sendRequestMessage.html',
            controller: 'SendRequestMessageModalController',
            scope: $scope
        });
	}
	
});	

app.controller('SendRequestMessageModalController', function($scope, $modalInstance, MessageServiceFactory){

	$scope.cancelRequestMessage = function(){
		$modalInstance.close();
	}
	
	$scope.sendRequestMessage = function(){
		MessageServiceFactory.getService().sendRequestMessage($scope.message).then(function(res){
			$modalInstance.close();
			toastr.success("Success!");
		}, function(error){
			toastr.error("Error!");
		});
	}
	
});

app.controller('MessageController', function($scope, $state, MessageServiceFactory){
	
	$scope.conversations = [];
	$scope.getUserConversations = function(){
		MessageServiceFactory.getService().getUserConversations().then(function(res){
			$scope.conversations = res.data;
			angular.forEach($scope.conversations, function(item){
				if(item.endUserPhotoId != null){
					item.endUserPhoto = "/getPhotoById/"+item.endUserPhotoId+"/ProfilePhoto";
				} else {
					item.endUserPhoto = "assets/images/default_user.png";
				}
			});
		}, function(error){
			toastr.error("Error!");
		});
	}
	
	$scope.viewConversationDetails = function(c){
		$state.go("messageDetails",({"cid":c.id}));
	}
	
});

app.controller('MessageDetailsController', function($scope, $stateParams, MessageServiceFactory){
	
	$scope.getConversationDetails = function(){
		MessageServiceFactory.getService().getConversationDetails($stateParams.cid).then(function(res){
			$scope.conversation = res.data.Conversation;
			$scope.messages = res.data.Messages;
			angular.forEach($scope.messages, function(item){
				item.createTime = moment(item.createTime).format("YYYY-MM-DD HH:mm:ss");
				var photoId;
				if(item.senderType == $scope.conversation.selfType){
					photoId = $scope.userBasicInfo.profilePhotoId;
				} else {
					photoId = $scope.conversation.endUserPhotoId;
				}
				if(photoId != null){
					item.userPhoto = "/getPhotoById/"+photoId+"/ProfilePhoto";
				} else {
					item.userPhoto = "assets/images/default_user.png";
				}
			});
		}, function(error){
			toastr.error("Error!");
		});
	}
	
	$scope.getConversationDetails();
	
	$scope.sendMessage = function(){
		$scope.message = {
			conversationId: $scope.conversation.id,
			content: $scope.messageText
		}
		MessageServiceFactory.getService().sendMessage($scope.message).then(function(res){
			toastr.success("Success!");
			$scope.messageText = "";
			$scope.getConversationDetails();
		}, function(error){
			toastr.error("Error!");
		});
	}
	
});
	