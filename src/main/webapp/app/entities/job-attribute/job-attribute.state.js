(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('job-attribute', {
            parent: 'entity',
            url: '/job-attribute',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'JobAttributes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job-attribute/job-attributes.html',
                    controller: 'JobAttributeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('job-attribute-detail', {
            parent: 'entity',
            url: '/job-attribute/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'JobAttribute'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job-attribute/job-attribute-detail.html',
                    controller: 'JobAttributeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'JobAttribute', function($stateParams, JobAttribute) {
                    return JobAttribute.get({id : $stateParams.id});
                }]
            }
        })
        .state('job-attribute.new', {
            parent: 'job-attribute',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-attribute/job-attribute-dialog.html',
                    controller: 'JobAttributeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                value: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('job-attribute', null, { reload: true });
                }, function() {
                    $state.go('job-attribute');
                });
            }]
        })
        .state('job-attribute.edit', {
            parent: 'job-attribute',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-attribute/job-attribute-dialog.html',
                    controller: 'JobAttributeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['JobAttribute', function(JobAttribute) {
                            return JobAttribute.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('job-attribute', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('job-attribute.delete', {
            parent: 'job-attribute',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-attribute/job-attribute-delete-dialog.html',
                    controller: 'JobAttributeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['JobAttribute', function(JobAttribute) {
                            return JobAttribute.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('job-attribute', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
