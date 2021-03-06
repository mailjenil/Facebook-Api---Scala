package com.fbapi

import akka.actor._
import akka.util.Timeout
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class RestInterface extends HttpServiceActor
with RestApi {

  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging { actor: Actor =>
  import com.danielasfregola.quiz.management.Entities._

  implicit val timeout = Timeout(10 seconds)

  var users = Vector[User]()
  var pages = Vector[Page]()

/* Random Users Generation*/
  for (i <- 1 to 500) {
    var idx = (new Random).nextInt(20)
    var userPages:Array[String] = new Array[String](idx)
    var friends:Array[String] = new Array[String](idx)
    for (j <- 0 to idx - 1) {
      userPages(j) = "p"+j.toString
      friends(j) = "f"+j.toString
    }
    var posts = Map("1" -> Array(i.toString, i.toString+"Hi"), "2" -> Array(i.toString,i.toString+"Hello"))
    val photos = Map("1" -> "Pic", "2" -> "Pic")
    val albums = Map("1" -> Map("1"+i.toString -> "Picture"), "2" -> Map("2" -> "Pic"))
    val foo = User(i.toString,"Anon"+i.toString,"Doe"+i.toString,(new Random).nextInt(70),"mail"+i.toString+"@gmail.com",userPages,friends,posts,photos,albums)
    var num1 = (new Random).nextInt(12345)
    var num2 = (new Random).nextInt(7)
    var num3 =(new Random).nextInt(10)
    var num4 =(new Random).nextInt(20)
    var foo_page = Page(i.toString, "About"+i.toString,"www.lol"+i.toString+".com",num1.toString, Array(num2.toString,num3.toString,num3.toString))
    users = users :+ foo
    pages = pages :+ foo_page
  }

  def routes: Route =
    pathPrefix("user") {
      pathPrefix(Segment) { id =>
        pathEnd {
          get { requestContext =>
            val responder = createResponder(requestContext)
            getUserInfo(id).map(responder ! _)
              .getOrElse(responder ! NotFound)
          } ~
            put {
              entity(as[Email]) { email => requestContext =>
                val responder = createResponder(requestContext)
                updateInfo(id, email)
                responder ! Created
              }
            }
        } ~
          pathPrefix("pages") {
            pathEnd {
              get { requestContext =>
                val responder = createResponder(requestContext)
                getPages(id).map(responder ! _)
                  .getOrElse(responder ! NotFound)
              }
            } ~
              path(Segment) { page =>
                pathEnd {
                  delete { requestContext =>
                    val responder = createResponder(requestContext)
                    println(page)
                    deletePage(id, page)
                    responder ! Deleted
                  }
                }
              }
          } ~
          pathPrefix("friendlist") {
            pathEnd {
              get { requestContext =>
                val responder = createResponder(requestContext)
                getFriends(id).map(responder ! _)
                  .getOrElse(responder ! NotFound)
              }
            }
          } ~
          pathPrefix("posts") {
            path(Segment) { postid =>
              pathEnd {
                get { requestContext =>
                  val responder = createResponder(requestContext)
                  getPost(id, postid).map(responder ! _)
                    .getOrElse(responder ! NotFound)
                } ~
                  delete { requestContext =>
                    val responder = createResponder(requestContext)
                    deletePost(id, postid)
                    responder ! Deleted
                  }
              }
            }
          } ~
          pathPrefix("post") {
            pathEnd {
              post {
                entity(as[PostPost]) { post => requestContext =>
                  val responder = createResponder(requestContext)
                  createPost(id, post)
                  responder ! Created
                }
              } ~
                put {
                  entity(as[PostPost]) { post => requestContext =>
                    val responder = createResponder(requestContext)
                    updatePost(id, post) match {
                      case true => responder ! Updated
                      case false => responder ! NotFound
                    }
                  }
                }
            }
          } ~
          pathPrefix("photos") {
            pathEnd {
              post {
                entity(as[PostPhoto]) { photo => requestContext =>
                  val responder = createResponder(requestContext)
                  createPhoto(id, photo)
                  responder ! Created
                }
              }
            } ~
              path(Segment) { photoid =>
                pathEnd {
                  get { requestContext =>
                    val responder = createResponder(requestContext)
                    getPhoto(id, photoid).map(responder ! _)
                      .getOrElse(responder ! NotFound)
                  } ~
                    delete { requestContext =>
                      val responder = createResponder(requestContext)
                      deletePhoto(id, photoid)
                      responder ! Deleted
                    } ~
                    put {
                      entity(as[Photo]) { photo => requestContext =>
                        val responder = createResponder(requestContext)
                        updatePhoto(id, photo) match {
                          case true => responder ! Updated
                          case _ => responder ! NotFound
                        }
                      }
                    }
                }
              }
          } ~
          pathPrefix("albums") {
            pathEnd {
              post {
                entity(as[PostAlbum]) { album => requestContext =>
                  val responder = createResponder(requestContext)
                  createAlbum(id, album)
                  responder ! Created
                }
              }
            } ~
              path(Segment) { albumid =>
                pathEnd {
                  get { requestContext =>
                    val responder = createResponder(requestContext)
                    getAlbum(id, albumid).map(responder ! _)
                      .getOrElse(responder ! NotFound)
                  }
                }
              }
          } ~
          pathPrefix("info") {
            pathEnd {
              get { requestContext =>
                val responder = createResponder(requestContext)
                getInfo(id).map(responder ! _)
                  .getOrElse(responder ! NotFound)
              }
            }
          }
      }
    }~
      pathPrefix("page") {
        pathPrefix(Segment) { id =>
          pathEnd {
            get { requestContext =>
              val responder = createResponder(requestContext)
              getPageInfo(id).map(responder ! _)
                .getOrElse(responder ! NotFound)
            } ~
              put {
                entity(as[Website]) { website => requestContext =>
                  val responder = createResponder(requestContext)
                  updatePage(id, website)
                  responder ! Updated
                }
              }
          } ~
            path("pages") {
              pathEnd {
                get { requestContext =>
                  val responder = createResponder(requestContext)
                  getPagesofPage(id).map(responder ! _)
                    .getOrElse(responder ! NotFound)
                }
              }
            } ~
            path(Segment) { pageid =>
              pathEnd {
                delete { requestContext =>
                  val responder = createResponder(requestContext)
                  deletePageofPage(id, pageid)
                  responder ! Deleted
                }
              }
            }
        }
      }

  private def createResponder(requestContext:RequestContext) = {
    context.actorOf(Props(new Responder(requestContext)))
  }

  private def getInfo(id:String) : Option[Info]= {
    val infoMap = Map("Pages" -> getUser(id).get.pages.size.toString, "Photos" -> getUser(id).get.photos.size.toString, "Albums" -> getUser(id).get.albums.size.toString, "Posts" -> getUser(id).get.posts.size.toString)
    Some(toInfo(infoMap))
  }

  private def createPost(id:String, post: PostPost) = {
    var currentUser = getUser(id).get
    var currentPostsMap = getUser(id).map(toPosts).get
    var currentPosts = currentPostsMap.posts
    var postid = (currentPosts.size + 1).toString
    var postedby = post.userid
    var posttext = post.post

    currentPosts += postid -> Array(postedby, posttext)

    var new_update = currentUser.copy(posts = currentPosts)
    users = users.filterNot(_.id == id)
    users = users :+ new_update
  }

  private def createPhoto(id:String, photo: PostPhoto) = {
    val currentUser = getUser(id).get
    var currentPhotosMap = getUser(id).map(toPhotos).get.photos
    var currentAlbumsMap = getUser(id).map(toPhotos).get.albums
    val idx = (currentPhotosMap.size + 1).toString

    currentPhotosMap += idx -> photo.photo

    val elem = currentAlbumsMap.get(photo.albumid)
    currentAlbumsMap += photo.albumid -> Map(idx -> photo.photo)

    val new_update = currentUser.copy(photos = currentPhotosMap, albums = currentAlbumsMap)
    users = users.filterNot(_.id == id)
    users = users :+ new_update
  }

  private def createAlbum(id:String, album: PostAlbum) = {
    val currentUser = getUser(id).get
    var currentAlbumsMap = getUser(id).map(toPhotos).get.albums
    currentAlbumsMap += album.id -> Map()
    val new_update = currentUser.copy(albums = currentAlbumsMap)
    users = users.filterNot(_.id == id)
    users = users :+ new_update
  }

  private def deletePage(id: String, page: String): Unit = {
    var user = getUser(id).get
    var pages = user.pages

    if (pages.contains(page)) {
      var pages_changed = pages.filter(! _.contains(page))
      var new_update = user.copy(pages = pages_changed)
      users = users.filterNot(_.id == id)
      users = users :+ new_update
    }
  }

  private def deletePost(id: String, postid: String): Unit = {
    var user = getUser(id).get
    var postMap = user.posts

    if (postMap.contains(postid)) {
      val newPostMap = postMap.filterKeys(_ != postid)
      var new_update = user.copy(posts = newPostMap)
      users = users.filterNot(_.id == id)
      users = users :+ new_update
    }
  }

  private def deletePhoto(id: String, photoid: String): Unit = {
    var user = getUser(id).get
    var photosMap = user.photos

    if (photosMap.contains(photoid)) {
      val newPhotosMap = photosMap.filterKeys(_ != photoid)
      var new_update = user.copy(photos = newPhotosMap)
      users = users.filterNot(_.id == id)
      users = users :+ new_update
    }
  }

  private def deletePageofPage(id: String, pageid: String): Unit = {
    var page = getPage(id).get
    var pageArray = page.pages

    if (pageArray.contains(pageid)) {
      val newPageArray = pageArray.filter(_ != pageid)
      var new_update = page.copy(pages = newPageArray)
      pages = pages.filterNot(_.id == id)
      pages = pages :+ new_update
    }
  }

  private def getPost(id: String, postid:String): Option[Post] = {
    var currentPosts = getUser(id).map(toPosts).get
    var posttext = currentPosts.posts(postid)(1)
    var postedby = currentPosts.posts(postid)(0)
    Some(toPost(postid, id, postedby, posttext))
  }

  private def getUserInfo(id: String): Option[UserView] = {
    getUser(id).map(toUserView)
  }

  private def getPageInfo(id: String): Option[PagesView] = {
    getPage(id).map(toPagesView)
  }

  private def getPagesofPage(id: String): Option[PagesofPage] = {
    getPage(id).map(toPagesofPage)
  }

  private def getFriends(id: String): Option[Friends] = {
    getUser(id).map(toFriends)
  }

  private def getPages(id: String): Option[PageView] = {
    getUser(id).map(toPageView)
  }

  private def getUser(id: String): Option[User] = {
    users.find(_.id == id)
  }

  private def getPage(id: String): Option[Page] = {
    pages.find(_.id == id)
  }

  private def getPhoto(id: String, photoid: String): Option[Photo] = {
    Some(toPhoto(photoid, getUser(id).map(toPhotos).get.photos(photoid)))
  }

  private def getAlbum(id: String, albumid: String): Option[Album] = {
    Some(toAlbum(id,getUser(id).map(toAlbums).get.albums(albumid)))
  }

  private def updateInfo(id: String, email: Email) = {
    val new_update = getUser(id).get.copy(email = email.email)
    users = users.filterNot(_.id == id)
    users = users :+ new_update
  }

  private def updatePage(id: String, website: Website) = {
    val new_update = getPage(id).get.copy(website = website.website)
    pages = pages.filterNot(_.id == id)
    pages = pages :+ new_update
  }

  private def updatePost(id: String, post: PostPost) : Boolean = {
    var postsMap = getUser(id).get.posts
    val containsornot = postsMap.contains(post.userid)
    if (containsornot){
      var postedby = postsMap(post.userid)(1)
      postsMap += post.userid -> Array(postedby, post.post)
      val new_update = getUser(id).get.copy(posts = postsMap)
      users = users.filterNot(_.id == id)
      users = users :+ new_update
    }
    containsornot
  }

  private def updatePhoto(id: String, photo: Photo): Boolean = {
    var user = getUser(id).get
    var photos = user.photos

    var containsornot = photos.contains(photo.id)
    if (photos.contains(photo.id)) {
      photos += photo.id -> photo.photo
    }
    var new_update = user.copy(photos = photos)
    users = users.filterNot(_.id == id)
    users = users :+ new_update

    containsornot
  }
}

class Responder(requestContext:RequestContext) extends Actor with ActorLogging {
  import com.danielasfregola.quiz.management.Entities._

  def receive = {

    case Created =>
      requestContext.complete(StatusCodes.Created)
      killYourself

    case Updated =>
      requestContext.complete(StatusCodes.Created)
      killYourself

    case Deleted =>
      requestContext.complete(StatusCodes.OK)
      killYourself

    case AlreadyExists =>
      requestContext.complete(StatusCodes.Conflict)
      killYourself

    case question: UserView =>
      requestContext.complete(StatusCodes.OK, question)
      killYourself

    case page: PageView =>
      requestContext.complete(StatusCodes.OK, page)
      killYourself

    case post: Post =>
      requestContext.complete(StatusCodes.OK, post)
      killYourself

    case friend: Friends =>
      requestContext.complete(StatusCodes.OK, friend)
      killYourself

    case page: PagesView =>
      requestContext.complete(StatusCodes.OK, page)
      killYourself

    case pagesofpage: PagesofPage =>
      requestContext.complete(StatusCodes.OK, pagesofpage)
      killYourself

    case photo: Photo =>
      requestContext.complete(StatusCodes.OK, photo)
      killYourself

    case album: Album =>
      requestContext.complete(StatusCodes.OK, album)
      killYourself

    case info: Info =>
      requestContext.complete(StatusCodes.OK, info)
      killYourself

    case NotFound =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself
  }
  private def killYourself = self ! PoisonPill
}
