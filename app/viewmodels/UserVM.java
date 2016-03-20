package viewmodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserVM {

	public Long id;
	public String firstName;
	public String lastName;
	public String gender;
	public Date dateOfBirth;
	public String aboutMe;
	public Long profilePhotoId;
	public List<LocationVM> placesBeenTos = new ArrayList<>();
}
