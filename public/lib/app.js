var app = angular.module('travelo', ['ui.router', 'oc.lazyLoad', 'ui.bootstrap']);

app.run(function($rootScope, $state) {
    $rootScope.$state = $state;
});