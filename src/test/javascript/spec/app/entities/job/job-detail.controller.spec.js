'use strict';

describe('Controller Tests', function() {

    describe('Job Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockJob, MockUser, MockJobAttribute;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockJob = jasmine.createSpy('MockJob');
            MockUser = jasmine.createSpy('MockUser');
            MockJobAttribute = jasmine.createSpy('MockJobAttribute');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Job': MockJob,
                'User': MockUser,
                'JobAttribute': MockJobAttribute
            };
            createController = function() {
                $injector.get('$controller')("JobDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'crowdwareApp:jobUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
