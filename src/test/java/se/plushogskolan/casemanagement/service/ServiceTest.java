package se.plushogskolan.casemanagement.service;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import se.plushogskolan.casemanagement.config.InmemoryDBConfig;
import se.plushogskolan.casemanagement.exception.ServiceException;
import se.plushogskolan.casemanagement.model.Issue;
import se.plushogskolan.casemanagement.model.Team;
import se.plushogskolan.casemanagement.model.User;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.casemanagement.model.WorkItem.Status;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { InmemoryDBConfig.class,
		CaseService.class }, loader = AnnotationConfigContextLoader.class)
public class ServiceTest {

	@Autowired
	CaseService caseService;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	Team rebelTeam;
	Team empireTeam;
	User lukeUser;
	User jackUser;
	User chewUser;
	User yodaUser;
	WorkItem yediWI;
	WorkItem jackWI;
	WorkItem flyWI;
	Issue hobbitIssue;
	Issue jackIssue;
	Issue chewIssue;

	@Before
	public void setup() {

		rebelTeam = caseService.save(new Team("Rebels").setActive(true));
		empireTeam = caseService.save(new Team("Empire").setActive(true));

		lukeUser = caseService.save(new User("Pewpewluke").setFirstName("Luke").setLastName("Skywalker").setActive(true)
				.setTeam(rebelTeam));
		jackUser = caseService.save(new User("CaptainJack").setFirstName("Jack").setLastName("Sparrow").setActive(true)
				.setTeam(empireTeam));

		chewUser = caseService.save(new User("Hairybeast").setFirstName("Chewbacka").setLastName("Something")
				.setActive(true).setTeam(rebelTeam));

		yodaUser = caseService.save(new User("Masteryoda").setFirstName("Yoda").setLastName("wrongheisnot")
				.setActive(true).setTeam(rebelTeam));

		yediWI = caseService.save(new WorkItem("Train to be a yedi", Status.DONE).setUser(lukeUser)
				.setIssue(hobbitIssue).setCreatedBy("Christian").setLastModifiedBy("Crille")
				.setCreatedDate(LocalDate.parse("2016-10-11", formatter))
				.setLastModifiedDate(LocalDate.parse("2016-11-20", formatter)));

		jackWI = caseService.save(new WorkItem("Recover the black pearl", Status.DONE).setUser(jackUser)
				.setIssue(jackIssue).setCreatedBy("Christian").setLastModifiedBy("Crille")
				.setCreatedDate(LocalDate.parse("2016-11-01", formatter))
				.setLastModifiedDate(LocalDate.parse("2016-11-30", formatter)));

		flyWI = caseService.save(new WorkItem("Fly you fools", Status.DONE).setUser(chewUser).setIssue(chewIssue)
				.setCreatedBy("Christian").setLastModifiedBy("Crille")
				.setCreatedDate(LocalDate.parse("2016-09-22", formatter))
				.setLastModifiedDate(LocalDate.parse("2016-10-05", formatter)));

		hobbitIssue = caseService.save(new Issue(yediWI, "They are taking the hobbits to isengarde"));
		jackIssue = caseService.save(new Issue(jackWI, "Why is the rum gone?"));
		chewIssue = caseService.save(new Issue(flyWI, "Cant speak to eagles"));
	}

	// USER

	@Test
	public void canSaveUser() {
		User user = caseService.save(new User("captainBarbosa"));
		User savedUser = caseService.getUser(user.getId());
		assertEquals("captainBarbosa", savedUser.getUsername());
	}
	
	@Test(expected = ServiceException.class)
	public void saveUserShouldThrowExceptionIfTeamIsFull(){
		
		User user = new User("userjoinfullteam");
		
		Team team = new Team("fullteam");
		
		caseService.save(team);
		
		for(int i = 0; i <= 10; i++){
			User newUser = caseService.save(new User("joinedfullteam" + i));
			
			caseService.addUserToTeam(newUser.getId(), team.getId());
		}
		
		user.setTeam(team);
		
		caseService.save(user);
	}

	@Test
	public void canGetUser() {
		User user = caseService.getUser(lukeUser.getId());
		assertEquals(lukeUser, user);
	}

	@Test
	public void canUpdateUserFirstName() {
		User user = caseService.getUser(lukeUser.getId());
		caseService.updateUserFirstName(user.getId(), "Leia");
		User updatedUser = caseService.getUser(user.getId());
		assertEquals("Leia", updatedUser.getFirstName());

	}
	
	@Test(expected = ServiceException.class)
	public void updateFirstNameShouldThrowExceptionIfNotExists() {

		caseService.updateUserFirstName(0l, "Test1234567");
	}


	@Test
	public void canUpdateUserLastName() {
		User user = caseService.getUser(lukeUser.getId());
		caseService.updateUserLastName(user.getId(), "Earthwalker");
		User updatedUser = caseService.getUser(user.getId());
		assertEquals("Earthwalker", updatedUser.getLastName());

	}
	
	@Test(expected = ServiceException.class)
	public void updateLastnameShouldThrowExceptionIfNotExists() {

		caseService.updateUserLastName(0l, "asdsadasd");
	}

	@Test
	public void canUpdateUserUserName() {
		User user = caseService.getUser(lukeUser.getId());
		caseService.updateUserUsername(user.getId(), "Lukeonehanded");
		User updatedUser = caseService.getUser(user.getId());
		assertEquals("Lukeonehanded", updatedUser.getUsername());

	}
	
	@Test(expected = ServiceException.class)
	public void updateUsernameShouldThrowExceptionIfNotExists() {

		caseService.updateUserUsername(0l, "Anakinskywalker");

	}

	@Test(expected = ServiceException.class)
	public void shouldThrowExceptionIfUsernameNotLongEnough() {
		caseService.updateUserUsername(1l, "short");
	}

	@Test
	public void canInactivateUser() {
		lukeUser = caseService.inactivateUser(lukeUser.getId());
		assertFalse(lukeUser.isActive());
	}

	@Test
	public void canActivateUser() {
		lukeUser = caseService.inactivateUser(lukeUser.getId());
		lukeUser = caseService.activateUser(lukeUser.getId());
		assertTrue(lukeUser.isActive());
	}

	@Test
	public void canSearchForUsersByFirstName() {
		User user1 = new User("Test123456").setFirstName("Luke");
		User user2 = new User("Test1234567").setFirstName("Luke");
		User user3 = new User("Test1234568").setFirstName("Luke");
		User user4 = new User("Test1234569").setFirstName("Luke");
		caseService.save(user1);
		caseService.save(user2);
		caseService.save(user3);
		caseService.save(user4);

		Slice<User> users = caseService.searchUsersByFirstName("Luke", new PageRequest(0, 5));
		int elements = users.getNumberOfElements();
		assertEquals(5, elements);

	}

	@Test
	public void canSearchForUsersByLastName() {
		User user1 = new User("Test123456").setLastName("Skywalker");
		User user2 = new User("Test1234567").setLastName("Skywalker");
		User user3 = new User("Test1234568").setLastName("Skywalker");
		User user4 = new User("Test1234569").setLastName("Skywalker");
		caseService.save(user1);
		caseService.save(user2);
		caseService.save(user3);
		caseService.save(user4);

		Slice<User> users = caseService.searchUsersByLastName("Skywalker", new PageRequest(0, 5));
		int elements = users.getNumberOfElements();
		assertEquals(5, elements);
	}

	@Test
	public void canSearchForUsersByUserName() {
		User user1 = new User("Test123456");
		User user2 = new User("Test1234567");
		User user3 = new User("Test1234568");
		User user4 = new User("Test1234569");
		caseService.save(user1);
		caseService.save(user2);
		caseService.save(user3);
		caseService.save(user4);

		Slice<User> users = caseService.searchUsersByUsername("Test", new PageRequest(0, 5));
		int elements = users.getNumberOfElements();
		assertEquals(4, elements);
	}

	@Test
	public void canGetUserByTeam() {
		Slice<User> users = caseService.getUsersByTeam(rebelTeam.getId(), new PageRequest(0, 5));
		int elements = users.getNumberOfElements();
		assertEquals(3, elements);
	}
	
	@Test
	public void canAddUserToTeam() {
		User user = caseService.save(new User("Test1234567"));
		Team team = caseService.save(new Team("TestTeam"));
		user = caseService.addUserToTeam(user.getId(), team.getId());
		for (User u : team.getUsers()) {
			if (u.getUsername().equals("Test1234567")) {
				assertEquals("Test1234567", u.getUsername());
			}
		}
	}

	// TEAM

	@Test
	public void canSaveTeam() {
		Team team = caseService.save(new Team("SavedTeam"));
		Team savedTeam = caseService.getTeam(team.getId());
		assertEquals("SavedTeam", savedTeam.getName());
	}

	@Test
	public void canUpdateTeam() {
		Team team = caseService.getTeam(rebelTeam.getId());
		caseService.updateTeam(team.getId(), team.setName("Carabien"));
		Team updatedTeam = caseService.getTeam(team.getId());
		assertEquals("Carabien", updatedTeam.getName());
	}

	@Test
	public void canInactivateTeam() {
		rebelTeam = caseService.inactivateTeam(rebelTeam.getId());
		assertFalse(rebelTeam.isActive());
	}

	@Test
	public void canActivateTeam() {
		rebelTeam = caseService.inactivateTeam(rebelTeam.getId());
		rebelTeam = caseService.activateTeam(rebelTeam.getId());
		assertTrue(rebelTeam.isActive());
	}

	@Test
	public void canGetTeam() {
		Team team = caseService.getTeam(rebelTeam.getId());
		assertEquals(team.getName(), rebelTeam.getName());
	}

	@Test
	public void searchTeamByName() {
		Team team = caseService.searchTeamByName("Rebels");
		assertEquals("Rebels", team.getName());
	}

	@Test
	public void getAllTeams() {
		Slice<Team> teams = caseService.getAllTeams(new PageRequest(0, 10));
		int elements = teams.getNumberOfElements();
		assertEquals(2, elements);
	}

	// WORKITEM

	@Test
	public void canSaveWorkItem() {
		WorkItem wi = caseService.save(new WorkItem("Saved", Status.UNSTARTED));
		WorkItem savedWI = caseService.getWorkItem(wi.getId());
		assertEquals("Saved", savedWI.getDescription());
	}

	@Test
	public void canUpdateWorkItem() {
		WorkItem workItem = caseService.getWorkItem(jackWI.getId());
		caseService.updateStatusById(workItem.getId(), Status.UNSTARTED);
		assertEquals(Status.UNSTARTED, workItem.getStatus());
	}

	@Test
	public void canAddWorkItemToUser() {
		User user = caseService.save(new User("Test1234567").setActive(true));
		WorkItem wi = caseService.save(new WorkItem("TestWI", Status.UNSTARTED));

		wi = caseService.addWorkItemToUser(wi.getId(), user.getId());

		assertEquals("Test1234567", wi.getUser().getUsername());

	}

	@Test
	public void canGetWorkItemsByDescription() {

		WorkItem wi1 = new WorkItem("Test1", Status.DONE);
		WorkItem wi2 = new WorkItem("Test1", Status.DONE);
		WorkItem wi3 = new WorkItem("Test1", Status.DONE);
		WorkItem wi4 = new WorkItem("Test1", Status.DONE);

		caseService.save(wi1);
		caseService.save(wi2);
		caseService.save(wi3);
		caseService.save(wi4);

		Slice<WorkItem> workItems = caseService.searchWorkItemByDescription("Test", new PageRequest(0, 10));
		int elements = workItems.getNumberOfElements();
		assertEquals(4, elements);
	}

	@Test
	public void canGetWorkItem() {
		WorkItem workItemFromDb = caseService.getWorkItem(yediWI.getId());
		assertEquals(workItemFromDb, yediWI);
	}

	@Test
	public void canGetWorkItemsByStatus() {
		WorkItem wi1 = new WorkItem("Test1", Status.DONE);
		WorkItem wi2 = new WorkItem("Test1", Status.DONE);
		WorkItem wi3 = new WorkItem("Test1", Status.UNSTARTED);
		WorkItem wi4 = new WorkItem("Test1", Status.DONE);

		caseService.save(wi1);
		caseService.save(wi2);
		caseService.save(wi3);
		caseService.save(wi4);

		Slice<WorkItem> workItems = caseService.getWorkItemsByStatus(Status.DONE, new PageRequest(0, 10));
		int elements = workItems.getNumberOfElements();
		assertEquals(3, elements);
	}

	@Test
	public void canGetWorkItemsByTeamId() {

		Slice<WorkItem> workItems = caseService.getWorkItemsByTeamId(rebelTeam.getId(), new PageRequest(0, 10));
		int elements = workItems.getNumberOfElements();
		assertEquals(2, elements);
	}

	@Test
	public void canGetWorkItemsByUserId() {
		WorkItem wi1 = caseService.save(new WorkItem("Test123", Status.UNSTARTED));
		WorkItem wi2 = caseService.save(new WorkItem("Test123", Status.UNSTARTED));
		User user = caseService.save(new User("Test1234567").setActive(true));
		caseService.addWorkItemToUser(wi1.getId(), user.getId());
		caseService.addWorkItemToUser(wi2.getId(), user.getId());

		Slice<WorkItem> workItems = caseService.getWorkItemsByUserId(user.getId(), new PageRequest(0, 10));
		int elements = workItems.getNumberOfElements();
		assertEquals(2, elements);
	}

	@Test
	public void canGetWorkItemsWithIssue() {
		Slice<WorkItem> workItems = caseService.getWorkItemsWithIssue(new PageRequest(0, 10));
		int elements = workItems.getNumberOfElements();
		assertEquals(3, elements);
	}
	
	@Test
	public void canGetWorkItemsWithStatusBetweenDates(){
		
		for(int i = 0; i < 5; i++){
			WorkItem wi = new WorkItem("test" + i, Status.DONE).setCreatedDate(LocalDate.parse("2016-10-10", formatter));
			caseService.save(wi);
		}
		
		Slice<WorkItem> workItems = caseService.getWorkItemsByPeriodAndStatus(Status.DONE, LocalDate.parse("2016-09-01", formatter), LocalDate.parse("2016-12-25", formatter), new PageRequest(0, 10));
		int elements = workItems.getNumberOfElements();
		for(WorkItem workItem : workItems){
			assertEquals(workItem.getStatus(), Status.DONE);
		}
		assertEquals(5, elements);
	}

	// ISSUE

	@Test
	public void canSaveIssue() {
		WorkItem wi = caseService.save(new WorkItem("Test", Status.DONE));
		Issue issue = caseService.save(new Issue(wi, "TestIssue"));
		assertEquals("TestIssue", issue.getDescription());
	}

	@Test
	public void canUpdateIssueDescription() {
		Issue issue = caseService.getIssue(jackIssue.getId());
		issue = caseService.updateIssueDescription(issue.getId(), "It is still gone");
		assertEquals("It is still gone", issue.getDescription());
	}

	@Test
	public void canGetIssue() {
		Issue issue = caseService.getIssue(jackIssue.getId());
		assertEquals(issue.getDescription(), jackIssue.getDescription());
	}

	@Test
	public void canGetAllIssues() {
		Slice<Issue> issues = caseService.getAllIssues(new PageRequest(0, 5));
		int elements = issues.getNumberOfElements();
		assertEquals(3, elements);
	}

}
