(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .controller('JobAttributeDialogController', JobAttributeDialogController);

    JobAttributeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'JobAttribute', 'Job'];

    function JobAttributeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, JobAttribute, Job) {
        var vm = this;
        vm.jobAttribute = entity;
        vm.jobs = Job.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('crowdwareApp:jobAttributeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.jobAttribute.id !== null) {
                JobAttribute.update(vm.jobAttribute, onSaveSuccess, onSaveError);
            } else {
                JobAttribute.save(vm.jobAttribute, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
