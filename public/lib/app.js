var app = angular.module('travelo', ['ui.router', 'oc.lazyLoad', 'ui.bootstrap']);

app.run(function($rootScope, $state) {
    $rootScope.$state = $state;
});

toastr.options = {
    "closeButton": true,
    "debug": false,
    "progressBar": true,
    "positionClass": "toast-top-right",
    "onclick": null,
    "showDuration": "400",
    "hideDuration": "1000",
    "timeOut": "7000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
}