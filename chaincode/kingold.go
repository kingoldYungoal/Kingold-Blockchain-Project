
package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	//"strconv"
	//"strings"
	//"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

// AutoTraceChaincode example simple Chaincode implementation
type AutoTraceChaincode struct {
}

type student struct {
	ObjectType      string `json:"docType"` //docType is used to distinguish the various types of objects in state database
	StudentId       string `json:"stu_id"`
	CrmId           string `json:"crm_id"` 
	StuEducationNo  string `json:"stu_education_no"`
	StuIdCardNo     string `json:"stu_idCard_no"`
	StuName         string `json:"stu_name"`
	StuOpertionTime string `json:"stu_opertion_time"`     	
	Remark          string `json:"remark"` 
}
type cert struct {
	ObjectType        string `json:"docType"` //docType is used to distinguish the various types of objects in state database
	CertId            string `json:"cert_id"`
	StuId             string `json:"stu_id"` 
	CertNo            string `json:"cert_no"`
	CertType          string `json:"cert_type"`
	CertHolder        string `json:"cert_holder"`
	CertName 	      string `json:"cert_name"`  
	CertContent   	  string `json:"cert_content"`  
	CertPdfPath       string `json:"cert_pdf_path"`  	
	CertHash      	  string `json:"cert_hash"`  	
	CertIssuer 		  string `json:"cert_issuer"` 	
	CertIssueDate     string `json:"cert_issue_date"`
	CertOperationTime string `json:"cert_operation_time"`
	CertStatus	      string `json:"cert_status"`  	
	Remark            string `json:"remark"` 
}
type event struct {
	ObjectType       string `json:"docType"` //docType is used to distinguish the various types of objects in state database
	EvtId            string `json:"evt_id"`
	StuId            string `json:"stu_id"` 
	EvtContent       string `json:"evt_content"`	
	EvtDate  	     string `json:"evt_date"`
	EvtOrg  	     string `json:"evt_org"`
	EvtOperationTime string `json:"evt_operation_time"`  	
	Remark           string `json:"remark"` 
}
type response struct {
	StudentJson       string `json:"valueJson"` 
}
// ===================================================================================
// Main
// ===================================================================================
func main() {
	err := shim.Start(new(AutoTraceChaincode))
	if err != nil {
		fmt.Printf("Error starting Parts Trace chaincode: %s", err)
	}
}

// Init initializes chaincode
// ===========================
func (t *AutoTraceChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

// Invoke - Our entry point for Invocations
// ========================================
func (t *AutoTraceChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)

	// Handle different functions
	if function == "initStudent" { //create a new student
		return t.initStudent(stub, args)
	} else if function == "insertCertinfo" { 
		return t.insertCertinfo(stub, args)
	} else if function == "readCert" { 
		return t.readCert(stub, args)
	} else if function == "insertEventInfo" { 
		return t.insertEventInfo(stub, args)
	} else if function == "readEvent" { 
		return t.readEvent(stub, args)
	} else if function == "queryCertByCRMId" {
		return t.queryCertByCRMId(stub, args)
	} else if function == "queryCertByEducationNo" { 
		return t.queryCertByEducationNo(stub, args)
	} else if function == "queryCertByIdCardNo" { 
		return t.queryCertByIdCardNo(stub, args)
	} else if function == "queryEventByCRMId" {
		return t.queryEventByCRMId(stub, args)
	} else if function == "queryEventByEducationNo" { 
		return t.queryEventByEducationNo(stub, args)
	} else if function == "queryEventByIdCardNo" { 
		return t.queryEventByIdCardNo(stub, args)
	} 
	/*else if function == "revokeCertStatus" { 
		return t.revokeCertStatus(stub, args)
	} */

	fmt.Println("invoke did not find func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

// ============================================================
// initStudent - create a new student, store into chaincode state
// ============================================================
func (t *AutoTraceChaincode) initStudent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	var err error

	
	// data model 
	//   0       	1      		2     		3				4						5	  6
	// "ser1234", "tata", "1502688979", "airbag 2020", "aaimler ag / mercedes", "false", "0"
	if len(args) != 7 {
		return shim.Error("Incorrect number of arguments. Expecting 7")
	}

	// ==== Input sanitation ====
	fmt.Println("- start init Student")
	if len(args[0]) <= 0 {
		return shim.Error("1st argument must be a non-empty string")
	}
	if len(args[1]) <= 0 {
		return shim.Error("2nd argument must be a non-empty string")
	}
	if len(args[2]) <= 0 {
		return shim.Error("3rd argument must be a non-empty string")
	}
	if len(args[3]) <= 0 {
		return shim.Error("4th argument must be a non-empty string")
	}
	if len(args[4]) <= 0 {
		return shim.Error("5th argument must be a non-empty string")
	}

	studentId := args[0]
	crmId := args[1]
	stuEducationNo  := args[2]
	stuIdCardNo := args[3]
	stuName := args[4]
	stuOperationTime := args[5]

	if err != nil {
		return shim.Error("6th argument must be a numeric string")
	}
	remark := args[6]

	// ==== Check if student already exists ====
	studentAsBytes, err := stub.GetState(studentId)
	if err != nil {
		return shim.Error("Failed to get student: " + err.Error())
	} else if studentAsBytes != nil {
		fmt.Println("This student already exists: " + studentId)
		return shim.Error("This student already exists: " + studentId)
	}

	// ==== Create student object and marshal to JSON ====
	objectType := "student"
	student := &student{objectType, studentId, crmId, stuEducationNo, stuIdCardNo, stuName, stuOperationTime, remark}
	studentJSONasBytes, err := json.Marshal(student)
	if err != nil {
		return shim.Error(err.Error())
	}

	// === Save student to state ===
	err = stub.PutState(studentId, studentJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	// ==== Student saved Return success ====
	fmt.Println("- end init student")
	return shim.Success(nil)
}
// ============================================================
// insertCertinfo - insert a new cert, store into chaincode state
// ============================================================
func (t *AutoTraceChaincode) insertCertinfo(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	var err error

	if len(args) != 14 {
		return shim.Error("Incorrect number of arguments. Expecting 14")
	}

	// ==== Input sanitation ====
	fmt.Println("- start init Cert")
	if len(args[0]) <= 0 {
		return shim.Error("1st argument must be a non-empty string")
	}
	if len(args[1]) <= 0 {
		return shim.Error("2nd argument must be a non-empty string")
	}
	if len(args[2]) <= 0 {
		return shim.Error("3rd argument must be a non-empty string")
	}
	if len(args[3]) <= 0 {
		return shim.Error("4th argument must be a non-empty string")
	}
	if len(args[4]) <= 0 {
		return shim.Error("5th argument must be a non-empty string")
	}
	if len(args[5]) <= 0 {
		return shim.Error("6th argument must be a non-empty string")
	}

	certId := args[0]
	stuId := args[1]
	certNo := args[2]
	certType:= args[3]
	certHolder := args[4]
	certName := args[5]
	certContent := args[6]
	certPdfPath := args[7]
	certHash := args[8]
	certIssuer := args[9]
	certIssueDate := args[10]
	certOperationTime := args[11]
	certStatus := args[12]
	remark := args[13]

	// ==== Check if Cert already exists ====
	certAsBytes, err := stub.GetState(certId)
	if err != nil {
		return shim.Error("Failed to get Cert: " + err.Error())
	} else if certAsBytes != nil {
		return shim.Error("This Cert already exists: " + certId)
	}

	// ==== Create cert object and marshal to JSON ====
	objectType := "cert"
	cert := &cert{objectType,certId,stuId,certNo,certType,certHolder,certName,certContent,certPdfPath,certHash,certIssuer,certIssueDate,certOperationTime,certStatus,remark}
	certJSONasBytes, err := json.Marshal(cert)
	if err != nil {
		return shim.Error(err.Error())
	}

	// === Save cert to state ===
	err = stub.PutState(certId, certJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== cert part saved and indexed. Return success ====
	fmt.Println("- end init cert")
	return shim.Success(nil)
}

// ===============================================
// readCert - read a cert part from chaincode state
// ===============================================
func (t *AutoTraceChaincode) readCert(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	var certId, jsonResp string
	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting cert_id of the cert to query")
	}

	certId = args[0]
	valAsbytes, err := stub.GetState(certId) //get the cert from chaincode state
	if err != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + certId + "\"}"
		fmt.Println(jsonResp)
		return shim.Error(jsonResp)
	} else if valAsbytes == nil {
		jsonResp = "{\"Error\":\"cert does not exist: " + certId + "\"}"
		fmt.Println(jsonResp)
		return shim.Error(jsonResp)
	}

	return shim.Success(valAsbytes)
}
// ============================================================
// insertEventInfo - insert a new Event, store into chaincode state
// ============================================================
func (t *AutoTraceChaincode) insertEventInfo(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	var err error

	if len(args) != 7 {
		return shim.Error("Incorrect number of arguments. Expecting 7")
	}

	// ==== Input sanitation ====
	fmt.Println("- start init event")
	if len(args[0]) <= 0 {
		return shim.Error("1st argument must be a non-empty string")
	}
	if len(args[1]) <= 0 {
		return shim.Error("2nd argument must be a non-empty string")
	}
	if len(args[2]) <= 0 {
		return shim.Error("3rd argument must be a non-empty string")
	}

	evtId := args[0]
	stuId := args[1]
	evtContent := args[2]
	evtDate := args[3]
	evtOrg := args[4]
	evtOperationTime := args[5]
	remark := args[6]
	// ==== Check if event already exists ====
	eventAsBytes, err := stub.GetState(evtId)
	if err != nil {
		return shim.Error("Failed to get event: " + err.Error())
	} else if eventAsBytes != nil {
		return shim.Error("This event already exists: " + evtId)
	}

	// ==== Create event object and marshal to JSON ====
	objectType := "event"
	event := &event{objectType,evtId,stuId,evtContent,evtDate,evtOrg,evtOperationTime,remark}
	eventJSONasBytes, err := json.Marshal(event)
	if err != nil {
		return shim.Error(err.Error())
	}

	// === Save event to state ===
	err = stub.PutState(evtId, eventJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== event part saved and indexed. Return success ====
	fmt.Println("- end init event")
	return shim.Success(nil)
}

// ===============================================
// readevent - read a event part from chaincode state
// ===============================================
func (t *AutoTraceChaincode) readEvent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	var evtId, jsonResp string
	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting evtId of the event to query")
	}

	evtId = args[0]
	valAsbytes, err := stub.GetState(evtId) //get the event from chaincode state
	if err != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + evtId + "\"}"
		fmt.Println(jsonResp)
		return shim.Error(jsonResp)
	} else if valAsbytes == nil {
		jsonResp = "{\"Error\":\"event does not exist: " + evtId + "\"}"
		fmt.Println(jsonResp)
		return shim.Error(jsonResp)
	}

	return shim.Success(valAsbytes)
}

// ===============================================
// queryCertByCRMId - query Certs By CRMId from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryCertByCRMId(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	return t.queryCertBySth(stub, args, "crm_id");
}
// ===============================================
// queryCertByEducationNo - query Certs By EducationNo from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryCertByEducationNo(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	return t.queryCertBySth(stub, args, "stu_education_no");
}
// ===============================================
// queryCertByIdCardNo - query Certs By idCardNo from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryCertByIdCardNo(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	return t.queryCertBySth(stub, args, "stu_idCard_no");
}
// ===============================================
// queryEventByCRMId - query Certs By CRMId from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryEventByCRMId(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	return t.queryEventBySth(stub, args, "crm_id");
}
// ===============================================
// queryEventByEducationNo - query Certs By EducationNo from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryEventByEducationNo(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	return t.queryEventBySth(stub, args, "stu_education_no");
}
// ===============================================
// queryEventByIdCardNo - query Certs By idCardNo from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryEventByIdCardNo(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	return t.queryEventBySth(stub, args, "stu_idCard_no");
}
// ===============================================
// queryCertBySth - query Certs By CRMId,idCardNo or eduNo from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryCertBySth(stub shim.ChaincodeStubInterface,args []string, sth string) peer.Response {
var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting "+ sth +" of a student to query")
	}
	arg:=args[0]
	// queryString := fmt.Sprintf("SELECT valueJson FROM <STATE> WHERE json_extract(valueJson, '$.docType') = 'cert' AND json_extract(valueJson, '$.stu_id') = '%s'")
	queryString := fmt.Sprintf("SELECT valueJson FROM <STATE> WHERE json_extract(valueJson, '$.docType') = 'student' AND json_extract(valueJson, '$.%s') = '%s'",sth,arg)
	
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	
	if err != nil {
		return shim.Error(err.Error())
	}
	
	var stuObjects []response
	err = json.Unmarshal(queryResults,&stuObjects)
	
	if err != nil {
		return shim.Error(err.Error())
	}
	if len(stuObjects)<1 {
		return shim.Error(sth + " : " + arg  + " is not existed")
	}
	var stu student
	json.Unmarshal([]byte(stuObjects[0].StudentJson),&stu)	
	if err != nil {
		return shim.Error(err.Error())
	}
	stuId:=stu.StudentId
	fmt.Println("got the specified student" + stuId)
	fmt.Println(stuObjects)
	queryString = fmt.Sprintf("SELECT valueJson FROM <STATE> WHERE json_extract(valueJson, '$.docType') = 'cert' AND json_extract(valueJson, '$.stu_id') = '%s'", stuId)
	
	queryResults, err = getQueryResultForQueryString(stub, queryString)
	
	return shim.Success(queryResults)
}
// ===============================================
// queryEventBySth - query Events By CRMId,idCardNo or eduNo from chaincode state
// ===============================================
func (t *AutoTraceChaincode) queryEventBySth(stub shim.ChaincodeStubInterface,args []string, sth string) peer.Response {
var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting "+ sth +" of a student to query")
	}
	arg:=args[0]
	// queryString := fmt.Sprintf("SELECT valueJson FROM <STATE> WHERE json_extract(valueJson, '$.docType') = 'cert' AND json_extract(valueJson, '$.stu_id') = '%s'")
	queryString := fmt.Sprintf("SELECT valueJson FROM <STATE> WHERE json_extract(valueJson, '$.docType') = 'student' AND json_extract(valueJson, '$.%s') = '%s'",sth,arg)
	
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	
	if err != nil {
		return shim.Error(err.Error())
	}
	
	var stuObjects []response
	err = json.Unmarshal(queryResults,&stuObjects)
	
	if err != nil {
		return shim.Error(err.Error())
	}
	if len(stuObjects)<1 {
		return shim.Error(sth + " : " + arg  + " is not existed")
	}
	var stu student
	json.Unmarshal([]byte(stuObjects[0].StudentJson),&stu)	
	if err != nil {
		return shim.Error(err.Error())
	}
	stuId:=stu.StudentId
	fmt.Println("got the specified student" + stuId)
	fmt.Println(stuObjects)
	queryString = fmt.Sprintf("SELECT valueJson FROM <STATE> WHERE json_extract(valueJson, '$.docType') = 'event' AND json_extract(valueJson, '$.stu_id') = '%s'", stuId)
	
	queryResults, err = getQueryResultForQueryString(stub, queryString)
	
	return shim.Success(queryResults)
}
func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {

	fmt.Printf("- getQueryResultForQueryString queryString:\n%s\n", queryString)

	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		fmt.Println(err.Error())
		return nil, err
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryRecords
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString(string(queryResponse.Value))
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())

	return buffer.Bytes(), nil
}