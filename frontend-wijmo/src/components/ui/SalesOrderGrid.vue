<template>
    <div style="max-height:80vh;">
        <div class="hierarchy">
            <div>수주 &nbsp; ></div>
            <div>&nbsp; 수주</div>
        </div>
        <div class="gs-bundle-of-buttons" style="max-height:10vh;">
            <v-btn @click="addNewRow" class="contrast-primary-text" small color="primary" :disabled="!hasRole('SalePerson')">
                <v-icon small>mdi-plus-circle-outline</v-icon>등록
            </v-btn>
            <v-btn  @click="editSelectedRow" class="contrast-primary-text" small color="primary" :disabled="!hasRole('SalePerson')">
                <v-icon small>mdi-pencil</v-icon>수정
            </v-btn>
            <v-btn @click="produceDialog = true" class="contrast-primary-text" small color="primary" >
                <v-icon small>mdi-minus-circle-outline</v-icon>생산완료
            </v-btn>
            <v-dialog v-model="produceDialog" width="500">
                <ProduceCommand
                    @closeDialog="produceDialog = false"
                    @produce="produce"
                ></ProduceCommand>
            </v-dialog>
            <v-btn @click="deleteSelectedRows" class="contrast-primary-text" small color="primary" :disabled="!hasRole('')">
                <v-icon small>mdi-minus-circle-outline</v-icon>삭제
            </v-btn>
            <excel-export-button class="contrast-primary-text" :exportService="this.exportService" :getFlex="getFlex" />
        </div>


        <!-- the grid -->
        <wj-flex-grid
            ref="flexGrid"
            :key="value.length"
            :autoGenerateColumns="false"
            :allowAddNew="false"
            :allowDelete="true"
            :allowPinning="'SingleColumn'"
            :newRowAtTop="false"
            :showMarquee="true"
            :selectionMode="'MultiRange'"
            :validateEdits="false"
            :itemsSource="value"
            :initialized="flexInitialized"
            :selectionChanged="onSelectionChanged"
            style="margin-top:10px; max-height:65vh;"
            class="wj-felx-grid"
        >
            <wj-flex-grid-filter :filterColumns="['RowHeader','salesOrderNumber','salesPerson','companyId','status','salesItems','salesType',]" />
            <wj-flex-grid-cell-template cellType="RowHeader" v-slot="cell">{{cell.row.index + 1}}</wj-flex-grid-cell-template>
            <wj-flex-grid-column binding="salesOrderNumber" header="수주 번호" width="2*" :isReadOnly="true" align="center" />
            <wj-flex-grid-column binding="salesPerson" header="수주 담당자" width="2*" :isReadOnly="true" align="center" />
            <wj-flex-grid-column binding="status" header="수주상태" width="2*" :isReadOnly="true" align="center" />
            <wj-flex-grid-column binding="salesType" header="SalesType" width="2*" :isReadOnly="true" align="center" />
            <wj-flex-grid-column binding="companyId" header="회사" width="2*" :isReadOnly="true" align="center">
                <wj-flex-grid-cell-template cellType="Cell" v-slot="cell">   
                    <CompanyId :editMode="editMode" v-model="cell.item.companyId"></CompanyId>
                </wj-flex-grid-cell-template>
            </wj-flex-grid-column>
        </wj-flex-grid>
        <v-col>
            <v-dialog
                v-model="openDialog"
                transition="dialog-bottom-transition"
                width="35%"
            >
                <template>
                    <v-card>
                        <v-toolbar
                            color="primary"
                            class="elevation-0"
                            height="50px"
                        >
                            <div style="color:white; font-size:17px; font-weight:700;">수주 등록</div>
                            <v-spacer></v-spacer>
                            <v-icon
                                color="white"
                                small
                                @click="closeDialog()"
                            >mdi-close</v-icon>
                        </v-toolbar>
                        <v-card-text>
                            <SalesOrder :offline="offline"
                                :isNew="!itemToEdit"
                                :editMode="true"
                                v-model="newValue"
                                @add="append"
                                @edit="edit"
                            />
                        </v-card-text>
                    </v-card>
                </template>
            </v-dialog>
        </v-col>
        <v-col style="margin-bottom:40px;">
            <div class="text-center">
                <v-dialog
                    width="332.5"
                    fullscreen
                    hide-overlay
                    transition="dialog-bottom-transition"
                >
                    <v-btn
                        style="position:absolute; top:2%; right:2%"
                        @click="closeDialog()"
                        depressed
                        icon 
                        absolute
                    >
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                </v-dialog>
            </div>
        </v-col>
    </div>
</template>

<script>
import SalesOrder from '../SalesOrder.vue'
import BaseGrid from '../base-ui/BaseGrid'

export default {
    name: 'salesOrderGrid',
    mixins:[BaseGrid],
    components:{
        SalesOrder,
    },
    data: () => ({
        path: 'salesOrders',
        produceDialog: false,
    }),
    watch: {
        newValue: {
            deep:true,
            handler:function(){
                if(!this.newValue){
                    this.newValue = {
                        'salesOrderNumber': '',
                        'salesPerson': '',
                        'companyId': {},
                        'status': '',
                        'salesItems': [],
                        'salesType': {},
                    }
                }
            }
        }
    },
    methods:{
        produce(params){
            try{
                this.repository.invoke(this.getSelectedItem(), "produce", params)
                this.$EventBus.$emit('show-success','produce 성공적으로 처리되었습니다.')
            }catch(e){
                this.$EventBus.$emit('show-error', e);
            }
        },
    }
}
</script>