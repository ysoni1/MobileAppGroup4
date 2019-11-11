from flask_restplus import Resource, reqparse
from flask_jwt import jwt_required
from models.doctor import DoctorModel

class DoctorAPI(Resource):
    def post(self):
        parser = reqparse.RequestParser()

        parser.add_argument('clinic', type=str, required=True, help="Field clinic cannot be empty")
        parser.add_argument('address', type=str, required=True, help="Field address cannot be empty")
        parser.add_argument('city', type=str, required=True, help="Field city cannot be empty")
        parser.add_argument('state', type=str, required=True, help="Field state cannot be empty")                
        parser.add_argument('zipcode', type=int, required=True, help="Field zipcode cannot be empty")
        parser.add_argument('phone', type=str, required=True, help="Field phone cannot be empty")                     
        parser.add_argument('hours', type=str, required=True, help="Field hours cannot be empty")                     
        parser.add_argument('contact', type=str, required=True, help="Field contact cannot be empty")
        parser.add_argument('pet_id', type=int, required=True, help="Field pet_id cannot be empty")                     

        payload = parser.parse_args()

        if DoctorModel.find_by_name(payload['clinic'], payload['pet_id']):
            return {'message' : f'Clinic {payload["clinic"]} already exits'}

        else:
            pet = DoctorModel(**payload)
            pet.save()

            return {'message' : 'Clinic created without issues'}, 201

class Doctor(Resource):
    def get(self, _id, pet_id):
        clinic = DoctorModel.find_by_id(_id, pet_id)

        if clinic:
            return clinic.json()
        else:
            return { 'message' : f'Clinic {_id} not found - pet : {pet_id}'}, 404

    def delete(self, _id, pet_id):
        clinic = DoctorModel.find_by_id(_id, pet_id)

        if clinic:
            clinic.delete()
            
            return { 'message' : f'Clinic {_id} deleted - pet : {pet_id}'}
        else:
            return { 'message' : f'Clinic {_id} not found - pet : {pet_id}'}, 404





class DoctorList(Resource):
    def get(self, pet_id):
        return { 'clinics' : [clinic.json() for clinic in DoctorModel.find_by_pet(pet_id)]}