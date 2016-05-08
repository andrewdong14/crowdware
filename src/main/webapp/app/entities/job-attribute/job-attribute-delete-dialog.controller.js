(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('JobAttributeDeleteController',JobAttributeDeleteController);

    JobAttributeDeleteController.$inject = ['$uibModalInstance', 'entity', 'JobAttribute'];

    function JobAttributeDeleteController($uibModalInstance, entity, JobAttribute) {
        var vm = this;
        vm.jobAttribute = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            JobAttribute.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
