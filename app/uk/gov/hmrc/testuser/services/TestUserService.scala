/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.testuser.services

import javax.inject.Inject
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.testuser.connectors.ApiPlatformTestUserConnector
import uk.gov.hmrc.testuser.models.{TestUser, UserTypes}
import uk.gov.hmrc.testuser.models.ServiceNames._
import uk.gov.hmrc.testuser.models.UserTypes.UserType

import scala.concurrent.Future

class TestUserService @Inject()(apiPlatformTestUserConnector: ApiPlatformTestUserConnector) {

  def createUser(userType: UserType)(implicit hc: HeaderCarrier): Future[TestUser] = {
    userType match {
      case UserTypes.INDIVIDUAL => apiPlatformTestUserConnector.createIndividual(Seq(NATIONAL_INSURANCE, SELF_ASSESSMENT, MTD_INCOME_TAX))
      case UserTypes.ORGANISATION => apiPlatformTestUserConnector.createOrganisation(Seq(NATIONAL_INSURANCE, SELF_ASSESSMENT, MTD_INCOME_TAX,
            CORPORATION_TAX, PAYE_FOR_EMPLOYERS, SUBMIT_VAT_RETURNS))
    }
  }
}

