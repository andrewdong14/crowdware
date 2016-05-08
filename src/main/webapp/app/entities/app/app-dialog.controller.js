(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('AppDialogController', AppDialogController);

    AppDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'App', 'User'];

    function AppDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, App, User) {
        var vm = this;
        vm.app = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('crowdwareApp:appUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.app.id !== null) {
                App.update(vm.app, onSaveSuccess, onSaveError);
            } else {
                App.save(vm.app, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
