(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('JobDeleteController',JobDeleteController);

    JobDeleteController.$inject = ['$uibModalInstance', 'entity', 'Job'];

    function JobDeleteController($uibModalInstance, entity, Job) {
        var vm = this;
        vm.job = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Job.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
