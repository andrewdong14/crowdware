(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('PaymentController', PaymentController);

    PaymentController.$inject = ['$scope', '$state', 'Payment', 'PaymentSearch'];

    function PaymentController ($scope, $state, Payment, PaymentSearch) {
        var vm = this;
        vm.payments = [];
        vm.loadAll = function() {
            Payment.query(function(result) {
                vm.payments = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            PaymentSearch.query({query: vm.searchQuery}, function(result) {
                vm.payments = result;
            });
        };
        vm.loadAll();
        
    }
})();
