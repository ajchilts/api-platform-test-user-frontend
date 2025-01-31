/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.testuser.connectors

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.ws.{WSClient, WSProxyServer, WSRequest }
import uk.gov.hmrc.http.Authorization
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.http.ws.{WSProxy, WSProxyConfiguration}
import play.api.http.HeaderNames

@Singleton
class ProxiedHttpClient @Inject() (config: Configuration, auditConnector: HttpAuditing, wsClient: WSClient, environment: play.api.Environment, actorSystem: ActorSystem)
    extends DefaultHttpClient(config, auditConnector, wsClient, actorSystem) with WSProxy {

  import ProxiedHttpClient._

  val authorization: Option[Authorization] = None

  def withAuthorization(bearerToken: String) = new ProxiedHttpClient(config, auditConnector, wsClient, environment, actorSystem) {
    override val authorization = Some(Authorization(s"Bearer $bearerToken"))
  }

  override def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration.buildWsProxyServer(config)

  override def buildRequest(url: String, headers: Seq[(String, String)]): WSRequest = {
    val effectiveHeaders = ACCEPT_HMRC_JSON_HEADER +: replaceAnyAuthHeader(authorization)(headers)

    super.buildRequest(url, effectiveHeaders)
  }
}

object ProxiedHttpClient {

  def removeAnyAuthHeader(in: Seq[(String, String)]): Seq[(String, String)] = {
    in.filterNot(hdr => hdr._1 == HeaderNames.AUTHORIZATION)
  }

  def replaceAnyAuthHeader(newValue: Option[Authorization])(in: Seq[(String, String)]): Seq[(String, String)] =
    removeAnyAuthHeader(in) ++
      newValue.map(auth => (HeaderNames.AUTHORIZATION -> auth.value)).toSeq

  val ACCEPT_HMRC_JSON_HEADER = HeaderNames.ACCEPT -> "application/hmrc.vnd.1.0+json"
}
