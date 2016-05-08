(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('PaymentDetailController', PaymentDetailController);

    PaymentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Payment', 'Job', 'User'];

    function PaymentDetailController($scope, $rootScope, $stateParams, entity, Payment, Job, User) {
        var vm = this;
        vm.payment = entity;
        
        var unsubscribe = $rootScope.$on('crowdwareApp:paymentUpdate', function(event, result) {
            vm.payment = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
