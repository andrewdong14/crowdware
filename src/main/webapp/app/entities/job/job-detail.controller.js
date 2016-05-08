(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('JobDetailController', JobDetailController);

    JobDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Job', 'User', 'JobAttribute'];

    function JobDetailController($scope, $rootScope, $stateParams, entity, Job, User, JobAttribute) {
        var vm = this;
        vm.job = entity;
        
        var unsubscribe = $rootScope.$on('crowdwareApp:jobUpdate', function(event, result) {
            vm.job = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
