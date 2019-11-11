import datetime as dt

from flask_restplus import Resource, reqparse
from flask_jwt import jwt_required
from models.pet import PetModel

class PetAPI(Resource):
    def post(self):
        parser = reqparse.RequestParser()

        parser.add_argument('name', type=str, required=True, help="Field name cannot be empty")
        parser.add_argument('sex', type=str, required=True, help="Field sex cannot be empty")
        parser.add_argument('type', type=str, required=True, help="Field type cannot be empty")
        parser.add_argument('breed', type=str, required=True, help="Field breed cannot be empty")                
        parser.add_argument('owner_id', type=int, required=True, help="Field owner_id cannot be empty")                
        parser.add_argument('bod', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d").date(), required=True, help="Field bod cannot be empty")                

        payload = parser.parse_args()

        if PetModel.find_by_name(payload['name'], payload['owner_id']):
            return {'message' : f'Pet {payload["name"]} already exits'}

        else:
            pet = PetModel(payload['name'], payload['sex'], payload['type'], payload['breed'], payload['owner_id'], payload['bod'])
            pet.save()

            return {'message' : 'Pet created without issues'}, 201

class Pet(Resource):
    def get(self, _id, owner):
        pet = PetModel.find_by_id(_id, owner)

        if pet:
            return pet.json()
        else:
            return { 'message' : f'Pet {_id} not found - owner : {owner}'}, 404

    def delete(self, _id, owner):
        pet = PetModel.find_by_id(_id, owner)

        if pet:
            pet.delete()

            return { 'message' : f'Pet {_id} deleted - owner : {owner}'}

        else:
            return { 'message' : f'Pet {_id} not found - owner : {owner}'}, 404

    def put(self, _id, owner):
        parser = reqparse.RequestParser()

        parser.add_argument('sex', type=str, required=True, help="Field sex cannot be empty")
        parser.add_argument('type', type=str, required=True, help="Field type cannot be empty")
        parser.add_argument('breed', type=str, required=True, help="Field breed cannot be empty")

        pet = PetModel.find_by_id(_id, owner)

        if pet:
            payload = parser.parse_args()

            pet.sex = payload['sex']
            pet.type = payload['type'] 
            pet.breed = payload['breed'] 

            pet.save()
            
            return { 'message' : f'Pet {_id} changed.'}

        else:
            return { 'message' : f'Pet {_id} not found - owner : {owner}'}, 404


class PetList(Resource):
    def get(self, owner):
        return { 'pets' : [pet.json() for pet in PetModel.find_by_owner(owner)]}



        



