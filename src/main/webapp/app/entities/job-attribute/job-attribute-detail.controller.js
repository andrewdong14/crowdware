(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('JobAttributeDetailController', JobAttributeDetailController);

    JobAttributeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'JobAttribute', 'Job'];

    function JobAttributeDetailController($scope, $rootScope, $stateParams, entity, JobAttribute, Job) {
        var vm = this;
        vm.jobAttribute = entity;
        
        var unsubscribe = $rootScope.$on('crowdwareApp:jobAttributeUpdate', function(event, result) {
            vm.jobAttribute = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
