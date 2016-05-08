(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('AppDeleteController',AppDeleteController);

    AppDeleteController.$inject = ['$uibModalInstance', 'entity', 'App'];

    function AppDeleteController($uibModalInstance, entity, App) {
        var vm = this;
        vm.app = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            App.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
