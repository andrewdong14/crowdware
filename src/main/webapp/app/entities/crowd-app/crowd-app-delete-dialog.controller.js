(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('CrowdAppDeleteController',CrowdAppDeleteController);

    CrowdAppDeleteController.$inject = ['$uibModalInstance', 'entity', 'CrowdApp'];

    function CrowdAppDeleteController($uibModalInstance, entity, CrowdApp) {
        var vm = this;
        vm.crowdApp = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            CrowdApp.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
