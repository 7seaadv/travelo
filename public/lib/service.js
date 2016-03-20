app.service('ImageUploadService', function($http, $q){
    return {
        uploadImage: function (file,originalPhotoId) {
            var defer = $q.defer();
            if(file && file != ""){
	            $.ajax("/uploadPhoto", {
	                type: "POST",
	                headers: {
	                    "File-name":  file.name
	                    },
	                data: file,
	                cache: false,
	                contentType: false,
	                processData: false,
	                success: function (data, status) {
	                    defer.resolve(data);
	                },
	                error: function(q,s,e) {
	                    defer.reject(q);
	                }
	            });
            } else {
            	var data = {
            		id: originalPhotoId
            	}
            	defer.resolve(data);
            }
            return defer.promise;
        }
    };
});

app.service('UtilService', function($http, $q){
    return {
    	getGooglePlaces: function (keyword) {
            var defer = $q.defer();
            if(keyword && keyword != ""){
	            $http.get('/getGooglePlaces/'+keyword).then(function (res) {
	                defer.resolve(res);
	            }, function (error) {
	                defer.reject(error);
	            });
            } else {
            	var result = {
        			data: {
        				predictions:[]	
        			}
            	}
            	defer.resolve(result);
            }
            return defer.promise;
        },
        getGooglePlaceDetails: function(pId){
        	var defer = $q.defer();
        	$http.get('/getGooglePlaceDetails/'+pId).then(function (res) {
        		defer.resolve(res);
        	}, function (error) {
        		defer.reject(error);
        	});
        	return defer.promise;
        }
    };
});

app.service('UserService', function($http, $q){
    return {
    	getCurrentUserProfile: function () {
            var defer = $q.defer();
            $http.get('/getCurrentUserProfile').then(function (res) {
                defer.resolve(res);
            }, function (error) {
                defer.reject(error);
            });
            return defer.promise;
        },
        updateCurrentUserProfile: function(data){
        	var defer = $q.defer();
            $http.post('/updateCurrentUserProfile',data).then(function (res) {
                defer.resolve(res);
            }, function (error) {
                defer.reject(error);
            });
            return defer.promise;
        },
        searchExperts: function (data) {
            var defer = $q.defer();
            $http.post('/searchExperts',data).then(function (res) {
                defer.resolve(res);
            }, function (error) {
                defer.reject(error);
            });
            return defer.promise;
        }
    };
});