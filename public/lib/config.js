
app.config(function($stateProvider, $urlRouterProvider, $ocLazyLoadProvider){
	
	$urlRouterProvider.otherwise("/");
    $ocLazyLoadProvider.config({
        // Set to true if you want to see what and when is dynamically loaded
        debug: false
    });

    $stateProvider
    .state('home', {
        url: "/",
        templateUrl: "assets/views/home.html"
    })
    .state('profile', {
        url: "/profile",
        templateUrl: "assets/views/profile.html",
        controller: "ProfileController",
        resolve: {
            loadPlugin: function ($ocLazyLoad) {
                return $ocLazyLoad.load([
					                 
                ]);
            }
        }
    })
    .state('editprofile', {
        url: "/editprofile",
        templateUrl: "assets/views/editProfile.html",
        controller: "EditProfileController",
        resolve: {
        	loadPlugin: function ($ocLazyLoad) {
        		return $ocLazyLoad.load([
					{
						name: 'angularFileUpload',
						files: ['assets/plugins/ng-file-upload/angular-file-upload-shim.min.js','assets/plugins/ng-file-upload/angular-file-upload.min.js']              
					}  
        		]);
        	}
        }
    })
    .state('findAnExpert', {
        url: "/find-an-expert",
        templateUrl: "assets/views/findAnExpert.html",
        controller: "FindAnExpertController",
        resolve: {
            loadPlugin: function ($ocLazyLoad) {
                return $ocLazyLoad.load([
					                 
                ]);
            }
        }
    })
    .state('messages', {
        url: "/messages",
        templateUrl: "assets/views/messages.html",
        controller: "MessageController",
        resolve: {
            loadPlugin: function ($ocLazyLoad) {
                return $ocLazyLoad.load([
					                 
                ]);
            }
        }
    })
    .state('messageDetails', {
        url: "/messages/:cid",
        templateUrl: "assets/views/messageDetails.html",
        controller: "MessageDetailsController",
        resolve: {
            loadPlugin: function ($ocLazyLoad) {
                return $ocLazyLoad.load([
					                 
                ]);
            }
        }
    })
});
    
