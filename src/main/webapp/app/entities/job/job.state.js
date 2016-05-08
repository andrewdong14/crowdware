(function() {
    'use strict';

    angular
        .module('crowdwareApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('job', {
            parent: 'entity',
            url: '/job?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Jobs'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job/jobs.html',
                    controller: 'JobController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('job-detail', {
            parent: 'entity',
            url: '/job/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Job'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job/job-detail.html',
                    controller: 'JobDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Job', function($stateParams, Job) {
                    return Job.get({id : $stateParams.id});
                }]
            }
        })
        .state('job.new', {
            parent: 'job',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job/job-dialog.html',
                    controller: 'JobDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                offer: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('job', null, { reload: true });
                }, function() {
                    $state.go('job');
                });
            }]
        })
        .state('job.edit', {
            parent: 'job',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job/job-dialog.html',
                    controller: 'JobDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Job', function(Job) {
                            return Job.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('job', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('job.delete', {
            parent: 'job',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job/job-delete-dialog.html',
                    controller: 'JobDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Job', function(Job) {
                            return Job.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('job', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
