(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('CrowdAppDialogController', CrowdAppDialogController);

    CrowdAppDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'CrowdApp', 'User'];

    function CrowdAppDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, CrowdApp, User) {
        var vm = this;
        vm.crowdApp = entity;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('crowdwareApp:crowdAppUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.crowdApp.id !== null) {
                CrowdApp.update(vm.crowdApp, onSaveSuccess, onSaveError);
            } else {
                CrowdApp.save(vm.crowdApp, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();
