<*
 * This is a sample .ts file. "ts" stands for "Tag Space". This tag space is able to describe the policies required
 * by HIPAA, Part2, FERPA, PPRA and the Government Records.
 * - Authors: Latanya Sweeney, Alexandra Wood, David O'Brien, Michael Bar-Sinai, clinical students at Berkman Center.
 *>

DataTags [This is the top level tag,
          used (by the tagging system) to describe the properties of the dataset]:
					consists of Code, Handling, Legal, Assertions. <-- Near future version of TS will specify which compound tag is to top level tag.
                                                         <--   For now, we go with a hard coded "DataTags".

Legal: consists of EducationRecords, <*HIPAA and more*> MedicalRecords, GovernmentRecords, ContractOrPolicy.
Assertions: consists of DataType, IP, Identity.

IP [Assertions pretaining to intellectual property]: TODO.

Code [This tag is actually going away soon, to become inferred by compliace sub-spaces (long story, read the paper when it's out)]: one of
	blue    [Non-confidential information that can be stored and shared freely.],
	green   [Potentially identifiable but not harmful personal information, shared with some access control.],
	yellow  [Potentially harmful personal information, shared with loosely verified and/or approved recipients.],
	orange  [May include sensitive, identifiable personal information, shared with verified and/or approved recipients under agreement.],
	red     [Very sensitive identifiable personal information, shared with strong verification of approved recipients under signed agreement.],
	crimson [Requires explicit permission for each transaction, using strong verification of approved recipients under signed agreement.]
.

Handling [practical and applicable aspects of data handling]: consists of
 Storage, Transit, Authentication,
 auth,
 DUA, Acceptance,
 Approval
 .

 Storage [The way data are stored on the server.]: one of
   clear [Not encrypted at all],
   serverEncrypt [Encryption on the server, "at rest". Attacker cannot use the data by getting the files from the file system],
   clientEncrypt [Encryption on the client side. Data obtained from the server (e.g. buy data breach or subpeona)
                  cannot be used unless the depositor provides the password],
   doubleEncrypt [Encryption on the client, and then on the server. Both passwords are required in order to make use of the data].

Transit [How the dataset should be transmitted]: one of
	clear         [ No encryption involved.],
	encrypt       [ Single encryption.],
	doubleEncrypt [ Encryption with two keys, which may be held by different parties. ]
.

Authentication: some of
	None     [Available to anonymous individuals.],
	Email    [Available to individuals with verified email address.],
	OAuth    [Available to individuals with verified online identity or a mobile phone.],
	Password [Available to individuals having a password accounts on system.]
.

DataType: consists of
	Effort, Harm.

Effort:    one of  anonymous, deidentified, identifiable, identified.
Harm:      one of  noRisk, minimal, shame, civil, criminal, maxControl.

DUA: consists of
	TimeLimit, Use,
	Sharing, Reidentify, Publication, Auditing.


TimeLimit: one of
 none  [Data stored indefinitely],
 _50yr [Data will be deleted after 50 years],
 _5yr  [Data will be deleted after 5 years],
 _2yr  [Data will be deleted after 2 years],
 _1yr  [Data will be deleted after 1 years].

Sharing: one of
		Anyone,
		NotOnline,
		Organization,
		Group,
		NoOne.

Reidentify [Under what condition may the data user reidentify the human subjects]: one of
	NoMatching,
	NoEntities,
	NoPeople,
	NoProhibition,
	Reidentify,
	Contact.

Publication: one of NoRestriction, Notify, PreApprove, Prohibited.

Use: one of NoRestriction, Research, IRB, NoProduct.

Acceptance: one of Click, Signed, SignWithID.

Approval: one of None, Email, Signed.

auth: one of approval, none.

Identity: one of
	noPersonData      [Data is about inanimate objects],
	notPersonSpecific [Data is about living people, but in aggregated form - where aggregation is over a group large enough],
	personSpecific    [Personal information can be directly inferred from the data].

Auditing: one of NotNeeded, Yearly, Monthly.

MedicalRecords: consists of HIPAA, Part2.
HIPAA: some of
		waiver
			[The data contain identifiable health information disclosed with waiver or alteration of authorization by an IRB or Privacy Board],
		authorization
			[The data contain identifiable health information disclosed for limited purposes with patient authorization],
		safeHarborDeidentified
			[The data contain health information that have been deidentified according to the HIPAA Privacy Rule safe harbor standard],
		expertDetermination
			[The data contain health information that have been deidentified using the HIPAA Privacy Rule expert determination method],
		limitedDataset
			[The data contain identifiable health information disclosed as a limited data set under the HIPAA Privacy Rule],
		businessAssociateContract
			[The data contain identifiable health information disclosed pursuant to a HIPAA business associate contract].

Part2: one of
	deidentified [
		The data contain deidentified information from patient records related to substance abuse diagnosis, referral, or treatment
	],
	veteransMedicalData [
		The data contain identifying information about substance abuse treatment that was released from US Veterans Affairs or Armed Services medical records
	],
	consent [
		The data contain identifying information about substance abuse treatment from records disclosed with the consent of the patient
	],
	scientificResearch [
		The data contain identifiable information about substance abuse treatment from records disclosed under the scientific research exception to the substance abuse confidentiality regulations
	].

EducationRecords: consists of FERPA, PPRA.
FERPA: some of
	deidentified [
		The data contain deidentified education records as defined in FERPA],
	directoryOptOut [
		The data contain directory information from students who have requested to opt out of the disclosure of this information],
	directoryInfo [
		The data contain identifiable information from education records designated by an educational agency or institution as directory information],
	schoolOfficial [
		The data contain identifiable information from education records disclosed under the school official exception to FERPA],
	study [
		The data contain identifiable information from education records disclosed under the studies exception to FERPA],
	consent [
		The data contain identifiable information from education records disclosed with the consent of the parents or students],
	audit [
		The data contain identifiable information from education records disclosed under the audit or evaluation exception to FERPA].

PPRA: some of
	protected
		[The data contain identifiable information that falls within one of the 8 categories of sensitive information protected under the PPRA],
	protectedDeidentified
	 	[The data contain deidentified information that falls within one of the 8 categories of sensitive information protected under the PPRA],
	consent
		[The data were collected with the prior written consent of the parents, or adult or emancipated minor students],
	optOutProvided
		[The data were collected after providing parents with notice and an opportunity to opt out of the collection and disclosure of the information],
	marketing
		[The data contain personal information collected from students for the purpose of marketing or sale].

GovernmentRecords: consists of DPPA, Census, ESRA, PrivacyAct, CIPSEA.
DPPA: some of highlyRestricted, required, stateConsentLimited, stateConsentBroad, requesterConsentLimited, requesterConsentBroad, research, exception.
Census: some of CensusPublished.
ESRA: some of restricted, public.
CIPSEA: some of deidentified, identifiable.
PrivacyAct: some of deidentified, identifiable.
ContractOrPolicy: one of
	no  [ Use or sharing of the data is not restricted by a contract or policy ],
	yes [ Use or sharing of the data is restricted by a contract or policy     ].
