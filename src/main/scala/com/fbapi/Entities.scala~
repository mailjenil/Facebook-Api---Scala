package com.fbapi

object Entities {

  import spray.json._

  case class User(id: String, firstname: String, lastname: String, age: Int, email: String, pages: Array[String], friends: Array[String], posts: Map[String,Array[String]], photos: Map[String, String], albums: Map[String, Map[String,String]])
  case class PageView(id: String, pages: Array[String])
  case class Friends(id: String, friends: Array[String])
  case class Posts(id: String, posts: Map[String,Array[String]])
  case class Post(postid: String, userid:String, postedby: String, post: String)
  case class PostPost(userid:String, post: String)
  case class UserView(id: String, firstname: String, lastname: String, age: Int, email: String)
  case class Page(id: String, about: String, website: String, phone: String, pages: Array[String])
  case class PagesView(id: String, about: String, website: String, phone: String)
  case class PagesofPage(pages: Array[String])
  case class Website(website: String)
  case class Photos(photos: Map[String, String], albums: Map[String, Map[String,String]])
  case class Photo(id: String, photo: String)
  case class PostPhoto(photo: String, albumid: String)
  case class Albums(albums: Map[String, Map[String,String]])
  case class Album(id: String, albums: Map[String,String])
  case class PostAlbum(id: String)
  case class Info(id: Map[String,String])
  case class Email(email: String)

  case object Created
  case object AlreadyExists
  case object Deleted
  case object NotFound
  case object Updated

  /* json (un)marshalling */

  object User extends DefaultJsonProtocol {
    implicit val format = jsonFormat10(User.apply)
  }

  object Friends extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Friends.apply)
  }

  object Posts extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Posts.apply)
  }

  object Post extends DefaultJsonProtocol {
    implicit val format = jsonFormat4(Post.apply)
  }

  object UserView extends DefaultJsonProtocol {
    implicit val format = jsonFormat5(UserView.apply)
  }

  object PageView extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(PageView.apply)
  }

  object Photo extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Photo.apply)
  }

  object Album extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Album.apply)
  }

  object Page extends DefaultJsonProtocol {
    implicit val format = jsonFormat5(Page.apply)
  }

  object PagesView extends DefaultJsonProtocol {
    implicit val format = jsonFormat4(PagesView.apply)
  }

  object PostPhoto extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(PostPhoto.apply)
  }

  object PostAlbum extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(PostAlbum.apply)
  }

  object PagesofPage extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(PagesofPage.apply)
  }

  object Info extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(Info.apply)
  }

  object Email extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(Email.apply)
  }

  object Website extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(Website.apply)
  }

  object PostPost extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(PostPost.apply)
  }

  /* implicit conversions */

  implicit def toUserView(user: User): UserView = UserView(id = user.id, firstname = user.firstname, lastname = user.lastname, age = user.age, email= user.email)
  implicit def toPageView(user: User): PageView = PageView(id = user.id, pages = user.pages)
  implicit def toFriends(user: User): Friends = Friends(id = user.id, friends = user.friends)
  implicit def toPosts(user: User): Posts = Posts(id = user.id, posts = user.posts)
  implicit def toPost(postid: String, userid: String, postedby: String, post: String): Post = Post(postid = postid, userid = userid, postedby = postedby, post = post)
  implicit def toPagesView(page: Page): PagesView = PagesView(id = page.id, about = page.about, website = page.website, phone = page.phone)
  implicit def toPagesofPage(page: Page): PagesofPage = PagesofPage(pages = page.pages)
  implicit def toWebsite(page: Page): Website = Website(website = page.website)
  implicit def toPhotos(user: User): Photos = Photos(photos = user.photos, albums = user.albums)
  implicit def toAlbums(user: User): Albums = Albums(albums = user.albums)
  implicit def toPhoto(id: String, photo: String): Photo = Photo(id = id, photo = photo)
  implicit def toAlbum(id: String, album: Map[String, String]): Album = Album(id = id, albums = album)
  implicit def toInfo(id: Map[String, String]): Info = Info(id = id)

}
