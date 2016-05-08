(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('AppDetailController', AppDetailController);

    AppDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'App', 'User'];

    function AppDetailController($scope, $rootScope, $stateParams, entity, App, User) {
        var vm = this;
        vm.app = entity;
        
        var unsubscribe = $rootScope.$on('crowdwareApp:appUpdate', function(event, result) {
            vm.app = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
