package com.example.musique.controllers;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.musique.entities.Musique;
import com.example.musique.entities.Role;
import com.example.musique.entities.Style;
import com.example.musique.entities.User;
import com.example.musique.service.StyleService;
import com.example.musique.service.UsersService;
import com.example.musique.service.MusiqueService;
import com.example.musique.service.RoleService;
@Controller
public class MusiqueController {
@Autowired
MusiqueService musiqueService;
@Autowired
StyleService StyleService;
@RequestMapping("/showCreate")
public String showCreate(ModelMap modelMap)
{
List<Style> cats = StyleService.findAll();
modelMap.addAttribute("styles", cats);
modelMap.addAttribute("musique", new Musique());
modelMap.addAttribute("mode", "new");
return "formMusique";
}

@RequestMapping("/saveMusique")
public String saveMusique(ModelMap modelMap,@Valid Musique musique,
BindingResult bindingResult)
{
	List<Style> cats = StyleService.findAll();
	modelMap.addAttribute("styles", cats);
System.out.println(musique);
if (bindingResult.hasErrors()) return "formMusique";
musiqueService.saveMusique(musique);
return "redirect:/listeMusique";
}

@RequestMapping("/showCreateStyle")
public String showCreateStyle(ModelMap modelMap)
{
modelMap.addAttribute("styles", new Style());
modelMap.addAttribute("mode", "new");
return "formStyle";
}
@RequestMapping("/saveStyle")
public String saveStyle(@ModelAttribute("style") Style style,ModelMap modelMap) throws ParseException 
{
Style saveStyle= StyleService.saveStyle(style);
return "redirect:/listeStyle";
}

@RequestMapping("/listeStyle")
public String listeMusique(ModelMap modelMap)
{
List <Style> cats = StyleService.findAll();
modelMap.addAttribute("styles", cats);
return "listeStyle";
}

@RequestMapping("/listeMusique")
public String listeMusique(ModelMap modelMap,
@RequestParam (name="page",defaultValue = "0") int page,
@RequestParam (name="size", defaultValue = "3") int size)
{
Page<Musique> musique = musiqueService.getAllMusiquesParPage(page, size);
modelMap.addAttribute("musiques", musique);
 modelMap.addAttribute("pages", new int[musique.getTotalPages()]);
modelMap.addAttribute("currentPage", page);
return "listeMusique";
}


@RequestMapping("/supprimerMusique")
public String supprimerMusique(@RequestParam("id") Long id,
ModelMap modelMap,
@RequestParam (name="page",defaultValue = "0") int page,
@RequestParam (name="size", defaultValue = "3") int size)
{
musiqueService.deleteMusiqueById(id);
Page<Musique> musique = musiqueService.getAllMusiquesParPage(page,size);
modelMap.addAttribute("musiques", musique);
modelMap.addAttribute("pages", new int[musique.getTotalPages()]);
modelMap.addAttribute("currentPage", page);
modelMap.addAttribute("size", size);
return "listeMusique";
}
@RequestMapping("/supprimerStyle")
public String supprimerStyle(@RequestParam("id") Long id,
 ModelMap modelMap)
{ 
StyleService.deleteStyleById(id);
List<Style> cats = StyleService.findAll();
modelMap.addAttribute("styles", cats);
return "ListeStyle";
}

@RequestMapping("/modifierMusique")
public String editerMusique(@RequestParam("id") Long id,ModelMap modelMap)
{
	Musique p= musiqueService.getMusique(id);
	List<Style> cats = StyleService.findAll();
	cats.remove(p.getStyle());
	modelMap.addAttribute("styles", cats);
	modelMap.addAttribute("musique", p);
	modelMap.addAttribute("mode", "edit");
	return "formMusique";

}
@RequestMapping("/updateMusique")
public String updateMusique(@ModelAttribute("musique") Musique musique,
@RequestParam("date") String date,ModelMap modelMap) throws ParseException {
	//conversion de la date 
	 SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	 Date dateCreation = dateformat.parse(String.valueOf(date));
	 musique.setDateCreation(dateCreation);
	 
	 musiqueService.updateMusique(musique);
	 List<Musique> m = musiqueService.getAllMusiques();
	 modelMap.addAttribute("musiques", m);
	return "listeMusique";
	}
@RequestMapping("/modifierStyle")
public String editerStyle(@RequestParam("id") Long id,ModelMap modelMap)
{
Style s= StyleService.getStyle(id);
modelMap.addAttribute("styles", s);
modelMap.addAttribute("mode", "edit");
return "formStyle";
}
@RequestMapping("/updateStyle")
public String updateStyle(@ModelAttribute("style") Style style,ModelMap modelMap) throws ParseException {
	StyleService.updateStyle(style);
	 List<Style> st = StyleService.findAll();
	 modelMap.addAttribute("styles", st);
	return "listeStyle";
	}

@RequestMapping("/rechercher")
public String rechercher(@RequestParam("nom") String nom,
ModelMap modelMap)
{
	List<Style> cats = StyleService.findAll();
	modelMap.addAttribute("styles", cats);
List<Musique> prods = musiqueService.findByNomMusique(nom);
modelMap.addAttribute("musiques",prods);
return "listeMusique";
}

@Autowired
RoleService roleService;
@Autowired
UsersService usersService;

@RequestMapping("/saveRole")
public String saveRole(@ModelAttribute("role") Role role, ModelMap modelMap) throws ParseException {
	Role saveRole = roleService.saveRole(role);
	return "redirect:/ListeUsers";
}

@RequestMapping("/showCreateUser")
public String showCreateUser(ModelMap modelMap) {
	List<Role> roles = roleService.findAll();
	modelMap.addAttribute("roles", roles);
	modelMap.addAttribute("users", new User());
	modelMap.addAttribute("mode", "new");
	return "FormUser";
}

@RequestMapping("/saveUser")
public String saveUser(@ModelAttribute("user") User user, ModelMap modelMap) throws ParseException {
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder() ;
	String encodedPassword = encoder. encode (user.getPassword());
	user.setPassword(encodedPassword);
	 usersService.saveUser(user);
	return "redirect:/ListeUsers";
}

@RequestMapping("/ListeUsers")
public String ListeUser(ModelMap modelMap) {
	List<User> users = usersService.findAll();
	modelMap.addAttribute("users", users);
	return "ListeUsers";
}

@RequestMapping("/supprimerUser")
public String supprimerUser(@RequestParam("id") Long id, ModelMap modelMap) {
	usersService.deleteUserById(id);
	List<User> roles = usersService.findAll();
	modelMap.addAttribute("users", roles);
	return "ListeUsers";
}


@RequestMapping("/modifierUser")
public String modifierUser(@RequestParam("id") Long id, ModelMap modelMap) {
	User u = usersService.getUser(id);
	List<Role> role = roleService.findAll();
	modelMap.addAttribute("roles", role);
	modelMap.addAttribute("users", u);
	modelMap.addAttribute("mode", "edit");
	return "FormUser";
}

@RequestMapping("/updateUser")
public String updateUser(@ModelAttribute("user") User user,ModelMap modelMap) throws ParseException {
	usersService.updateUser(user);
	 List<User> roles = usersService.findAll();
	 modelMap.addAttribute("users", roles);
	return "ListeUsers";
}
}
