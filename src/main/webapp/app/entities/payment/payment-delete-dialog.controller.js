(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('PaymentDeleteController',PaymentDeleteController);

    PaymentDeleteController.$inject = ['$uibModalInstance', 'entity', 'Payment'];

    function PaymentDeleteController($uibModalInstance, entity, Payment) {
        var vm = this;
        vm.payment = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Payment.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
