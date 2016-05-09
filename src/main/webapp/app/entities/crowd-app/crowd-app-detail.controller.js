(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('CrowdAppDetailController', CrowdAppDetailController);

    CrowdAppDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'CrowdApp', 'User'];

    function CrowdAppDetailController($scope, $rootScope, $stateParams, DataUtils, entity, CrowdApp, User) {
        var vm = this;
        vm.crowdApp = entity;
        
        var unsubscribe = $rootScope.$on('crowdwareApp:crowdAppUpdate', function(event, result) {
            vm.crowdApp = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();
