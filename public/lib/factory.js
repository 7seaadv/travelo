
app.factory('ImageUploadServiceFactory', function(ImageUploadService) {
    return {
        getService: function() {
        	return ImageUploadService;
        }
    };
});

app.factory('UtilServiceFactory', function(UtilService) {
    return {
        getService: function() {
        	return UtilService;
        }
    };
});

app.factory('UserServiceFactory', function(UserService) {
    return {
        getService: function() {
        	return UserService;
        }
    };
});
