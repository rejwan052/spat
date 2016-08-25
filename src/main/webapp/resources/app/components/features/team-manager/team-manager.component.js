define([
    "angular"
], function(angular) {
    "use strict";

    var teamManagerComponent = {
        templateUrl: "resources/app/components/features/team-manager/team-manager.component.html",
        controller: TeamManagerController,
        controllerAs: "vm"
    };

    TeamManagerController.$inject = ["modalService", "TeamResource"];

    function TeamManagerController(modalService, TeamResource) {
        var vm = this;
        vm.teams = null;

        vm.addUser = function() {
            modalService.showModal({
                component: "teamManagerModal",
                resolve: {
                    modalData: function() {
                        return {
                            action: "Add",
                            team: new TeamResource()
                        };
                    }
                }
            }).then(function(newTeam) {
                TeamResource.save(newTeam, function(response) {
                    vm.teams.push(response);
                });
            });
        };

        vm.editUser = function(team) {
            modalService.showModal({
                component: "teamManagerModal",
                resolve: {
                    modalData: function() {
                        return {
                            action: "Edit",
                            team: angular.copy(team)
                        };
                    }
                }
            }).then(function(updatedTeam) {
                TeamResource.save(updatedTeam, function(response) {
                    vm.teams[vm.teams.indexOf(team)] = response;
                });
            }, function(r) {
                console.info(r);
            });
        };

        vm.deleteUser = function(team) {
            modalService.showModal({
                resolve: {
                    modalData: function() {
                        return {
                            title: "Delete Team",
                            message: "Are you sure you want to delete " + team.name + "?",
                            includeCancelButton: true
                        };
                    }
                }
            }).then(function() {
                TeamResource.delete({id: team.id}, function() {
                    vm.teams.splice(vm.teams.indexOf(team), 1);
                });
            })
        };

        function activate() {
            TeamResource.query(function(response) {
                vm.teams = response;
            }, function(response) {
                alert("Unable to retrieve teams.  Response code:" + response.status);
            });
        }

        activate();

    }

    return teamManagerComponent;
});
